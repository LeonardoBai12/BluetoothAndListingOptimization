# ANR Lab: os 4 pares problema/solução lado a lado

*Event na prática — 4 problemas, 4 soluções, o mesmo trabalho em cada par*

O `AnrEvent` visto em [Event](./04-event.md) tem 8 variantes — 4 problemas e 4 soluções, em pares. Aqui estão as 4 implementações completas, direto de
[`AnrViewModel.kt`](../feature/anr/src/main/kotlin/io/lb/bleandlistingopt/feature/anr/AnrViewModel.kt): para cada par, o problema e a solução fazem **exatamente o mesmo trabalho** — a única diferença é qual thread faz esse trabalho.

## Par 1 — `Thread.sleep()`

*Problema — `OnTriggerSleep`*

```kotlin
private fun triggerSleep() {
    _state.update { it.copy(status = "Sleeping on main thread for 6s...") }
    Thread.sleep(6_000)
    _state.update { it.copy(status = "Woke up after 6s (main thread was frozen)") }
}
```

Chamado direto no click handler → roda na main thread. Não tem dispatcher nenhum envolvido, é a própria thread de UI que fica travada 6s.

*Solução — `OnFixSleep`*

```kotlin
private fun fixSleep() {
    _state.update { it.copy(status = "Waiting 6s off the main thread...") }
    viewModelScope.launch(dispatchers.default) {
        delay(6_000)
        _state.update { it.copy(status = "Done waiting 6s (main thread stayed responsive)") }
    }
}
```

`onEvent()` retorna na hora; `delay()` suspende a coroutine sem bloquear nenhuma thread.

## Par 2 — Loop de CPU pesado

*Problema — `OnTriggerCpuLoop`*

```kotlin
private fun triggerCpuLoop() {
    _state.update { it.copy(status = "Computing primes on main thread...") }
    val count = countPrimesFor(6_000)
    _state.update { it.copy(status = "Found $count primes (main thread was frozen)") }
}
```

Trabalho de CPU real (teste de primalidade por divisão), síncrono, no click handler.

*Solução — `OnFixCpuLoop`*

```kotlin
private fun fixCpuLoop() {
    _state.update { it.copy(status = "Computing primes off the main thread...") }
    viewModelScope.launch(dispatchers.default) {
        val count = countPrimesFor(6_000)
        _state.update { it.copy(status = "Found $count primes (main thread stayed responsive)") }
    }
}
```

Mesmo `countPrimesFor()`, só que dentro de `launch(dispatchers.default)` — ideal pra trabalho de CPU que não bloqueia em I/O. `countPrimesFor` é time-bounded (6s), não um limite fixo de números, porque um limite fixo rodaria em tempo real muito diferente em CPUs diferentes.

## Par 3 — Escrita bloqueante em disco

*Problema — `OnTriggerDiskRead`*

```kotlin
private fun triggerDiskRead() {
    _state.update { it.copy(status = "Writing to disk on main thread...") }
    blockingDiskWork(6_000)
    _state.update { it.copy(status = "Done writing (main thread was frozen)") }
}
```

Escritas síncronas com `fd.sync()` a cada pedaço — cada `sync()` bloqueia até o SO confirmar que o dado chegou no disco de verdade. É exatamente o tipo de chamada que a política de disk-write do StrictMode (ativada só nesta tela, veja [`StrictModeSetup.kt`](../feature/anr/src/main/kotlin/io/lb/bleandlistingopt/feature/anr/StrictModeSetup.kt)) sinaliza sozinha, independente de rodar tempo suficiente para também causar um ANR.

*Solução — `OnFixDiskRead`*

```kotlin
private fun fixDiskRead() {
    _state.update { it.copy(status = "Writing to disk off the main thread...") }
    viewModelScope.launch(dispatchers.io) {
        blockingDiskWork(6_000)
        _state.update { it.copy(status = "Done writing (main thread stayed responsive)") }
    }
}
```

Mesmo `blockingDiskWork()`, dentro de `launch(dispatchers.io)` — o dispatcher pensado justamente para I/O bloqueante.

## Par 4 — Deadlock de dois locks

*Problema — `OnTriggerDeadlock`*

```kotlin
private fun triggerDeadlock() {
    _state.update { it.copy(status = "Deadlocking...") }
    thread {
        synchronized(lockB) {
            Thread.sleep(200)
            synchronized(lockA) { /* nunca chega aqui */ }
        }
    }
    Thread.sleep(200)
    synchronized(lockA) {
        synchronized(lockB) { /* nem aqui, na main */ }
    }
}
```

Main thread trava `lockA` e tenta `lockB`; a thread nova trava `lockB` e tenta `lockA`. Cada uma espera para sempre pelo lock que a outra segura. O `sleep(200)` escalonado depois do primeiro lock é o que torna isso reproduzível de forma confiável: dá tempo para a outra thread pegar o próprio primeiro lock antes de qualquer uma tentar o segundo.

*Solução — `OnFixDeadlock`*

```kotlin
private fun fixDeadlock() {
    _state.update { it.copy(status = "Locking in consistent order...") }
    thread {
        synchronized(lockA) {
            Thread.sleep(200)
            synchronized(lockB) { /* ok */ }
        }
    }
    viewModelScope.launch(dispatchers.default) {
        delay(200)
        synchronized(lockA) {
            synchronized(lockB) { /* ok */ }
        }
        _state.update { it.copy(status = "No deadlock (consistent lock order)") }
    }
}
```

As duas threads agora travam `lockA` primeiro, sempre. Sem espera circular possível, não existe deadlock — só uma pequena espera de vez, nunca infinita.

---

Repare que os 4 problemas **e** as 4 soluções reaproveitam o mesmo trabalho de base (`countPrimesFor`, `blockingDiskWork`, os mesmos `lockA`/`lockB`). Isso não é coincidência — é o que prova que o problema nunca foi *o que* o código faz, e sim *em qual thread* ele roda.

---

[↑ Índice](./index.md) · [Anterior: Como as três peças se conectam](./06-fluxo-completo.md) · [Próximo: Armadilhas comuns →](./08-armadilhas-comuns.md)
