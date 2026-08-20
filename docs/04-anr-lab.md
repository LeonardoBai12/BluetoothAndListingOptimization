---
layout: default
title: "ANR Lab"
nav_order: 5
---

# ANR Lab

*4 problemas, 4 soluções, o mesmo trabalho em cada par*

O timeout de despacho de input do Android é de **~5s**: se a thread principal não devolver o controle nesse tempo depois de um toque, o sistema mostra o diálogo de "app não está respondendo". Os 4 pares abaixo, direto de
[`AnrViewModel.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/anr/src/main/kotlin/io/lb/bleandlistingopt/feature/anr/AnrViewModel.kt), travam a main thread por ~6s de formas diferentes — para cada par, o problema e a solução fazem **exatamente o mesmo trabalho**; a única diferença é qual thread faz esse trabalho.

Para o fluxo completo de como puxar e ler o trace de um ANR (`adb pull /data/anr/traces.txt`, como ler a stack da thread `"main"`) e como isso se conecta ao Crashlytics via `ApplicationExitInfo`, veja o
[README do módulo `feature:anr`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/anr/README.md).

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

Escritas síncronas com `fd.sync()` a cada pedaço — cada `sync()` bloqueia até o SO confirmar que o dado chegou no disco de verdade. É exatamente o tipo de chamada que a política de disk-write do StrictMode (ativada só nesta tela, veja [`StrictModeSetup.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/anr/src/main/kotlin/io/lb/bleandlistingopt/feature/anr/StrictModeSetup.kt)) sinaliza sozinha, independente de rodar tempo suficiente para também causar um ANR.

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

## StrictMode: o que é, e por que está aqui

`StrictMode` é uma ferramenta de desenvolvimento do próprio Android que instrumenta chamadas específicas — leitura/escrita de disco, chamadas de rede — feitas na main thread, e reage quando encontra uma. Ela existe porque nem todo "trabalho lento na main thread" é óbvio no código: um `SharedPreferences.getString()` parece inofensivo, mas por baixo é uma leitura de disco; uma chamada de biblioteca de terceiros pode esconder uma requisição de rede síncrona. StrictMode pega esses casos automaticamente, sem precisar saber de antemão onde procurar.

Ela é ativada só na tela do ANR Lab ([`AnrLabActivity.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/anr/src/main/kotlin/io/lb/bleandlistingopt/feature/anr/AnrLabActivity.kt)), não no app inteiro, e por um motivo concreto: ligá-la assim que o processo inicia (em `BleLabApplication`) pegava chamadas de disco/rede legítimas do próprio Firebase e do Dagger durante a inicialização — nada relacionado a nenhum anti-padrão deste projeto, só ruído, disparando o alerta em toda abertura do app.

```kotlin
fun enableStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .build(),
    )
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .build(),
    )
}
```

`penaltyLog()` só escreve a violação no Logcat (com o `Thread.sleep()`/CPU-loop/deadlock deste laboratório, ela não pega nada — só instrumenta chamadas de I/O, não "a thread ficou bloqueada"; é exatamente por isso que o timeout de ANR existe separadamente, para cobrir esse resto). O par "Escrita bloqueante em disco" é o único dos quatro que StrictMode consegue sinalizar sozinha, mesmo sem o app ter medido 6 segundos.

### Um gotcha real, encontrado testando este próprio guia

A primeira versão desta tela também usava `penaltyDialog()`, que mostra um diálogo modal na hora que a violação acontece — a opção mais visível, e a que o par teórico "penaltyLog + penaltyDialog" sempre descreve junto. Só que, testando no dispositivo, isso causou um ANR real e reprodutível: o próprio diálogo do StrictMode, sendo modal, bloqueia brevemente o despacho de eventos de input — e esse bloqueio sozinho foi suficiente para o *watchdog de ANR do sistema* (que é um mecanismo totalmente separado de StrictMode) entender que a `Activity` parou de responder, e abrir o diálogo genuíno de "app não está respondendo". Uma ferramenta de debug ativando o exato problema que ela existe para ajudar a diagnosticar.

A correção foi tirar `penaltyDialog()` e manter só `penaltyLog()`, que nunca bloqueia nada — só escreve no Logcat. Um segundo problema, relacionado mas distinto, apareceu junto: `StrictMode.setThreadPolicy`/`setVmPolicy` são configurações **globais do processo**, não presas ao ciclo de vida de uma `Activity`. Chamar `enableStrictMode()` só uma vez em `onCreate()` deixava a política ativa para sempre depois disso, em qualquer tela — inclusive telas de Bluetooth/Listagem sem relação nenhuma com o ANR Lab. A correção foi ligar em `onResume()` e desligar em `onPause()`:

```kotlin
override fun onResume() {
    super.onResume()
    enableStrictMode()
}

override fun onPause() {
    disableStrictMode()
    super.onPause()
}
```

`disableStrictMode()` restaura a política padrão (`StrictMode.ThreadPolicy.LAX`/`VmPolicy.LAX`) ao sair da tela, então "ativado" volta a significar, de verdade, "enquanto esta tela estiver em primeiro plano" — não "a partir de agora, para sempre, neste processo".

## Debugando: Android Studio

- **Profiler → Threads, ao vivo.** Abra o Profiler (View → Tool Windows → Profiler) *antes* de tocar em "Trigger ANR" e olhe a aba Threads. A thread `main` muda de verde para amarelo/vermelho assim que entra em `Thread.sleep`/trabalho de CPU/I/O. No **Par 4 (deadlock)**, é aqui que fica mais óbvio: o Profiler desenha as duas threads bloqueadas uma na outra, com o ícone de lock indicando exatamente qual monitor cada uma está esperando — é a forma mais rápida de *ver* uma espera circular sem precisar ler stack trace nenhuma.
- **Logcat detecta o ANR sozinho.** Quando o watchdog dispara, o Logcat mostra uma entrada `ActivityManager: ANR in io.lb.bleandlistingopt` com um link clicável "Show details" — abre a stack da main thread direto no editor, sem precisar puxar arquivo nenhum na mão.
- **Analisar um trace já puxado.** Depois de `adb pull /data/anr/traces.txt` (veja o [README do módulo](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/anr/README.md)), abra o arquivo no Android Studio e use **Code → Analyze Stack Trace** (ou cole o conteúdo direto no diálogo) — o Studio linka cada frame de volta para a linha exata do seu código-fonte.

## Debugando: console do Crashlytics

1. **Firebase console → Crashlytics → aba ANRs**, filtrando por versão do app ou dispositivo se precisar isolar o par específico que foi disparado.
2. Cada issue de ANR abre com a stack da thread `main` em destaque, mas a aba **Threads** do relatório mostra as *outras* threads também no mesmo instante — essencial no **Par 4**, onde a causa raiz está na thread que segura o lock, não na `main` que só está esperando.
3. A aba **Sessions** mostra o estado do dispositivo (memória, se estava em background, outras respirações do processo) no momento do ANR — útil para distinguir "trava genuína no meu código" de "o sistema matou o processo por outro motivo".
4. Como o Crashlytics lê o mesmo `ApplicationExitInfo`/`REASON_ANR` que o leitor local do app (veja o README do módulo), o relatório do console só aparece depois do delay normal de upload — a leitura local (`readLastAnrReason()`, mostrada na própria tela ao reabrir o app) é o jeito instantâneo de confirmar que o ANR foi de fato registrado, enquanto o console ainda não atualizou.

---

[↑ Índice](./) · [Anterior: Bluetooth (GATT)](./03-ble-gatt/)
