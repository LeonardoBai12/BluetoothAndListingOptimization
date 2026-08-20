# Android Fundamentals Lab

*Um projeto de estudo Android nativo para praticar, isolado e com código fartamente comentado, quatro áreas: otimização de RecyclerView, otimização de listas em Jetpack Compose, gerenciamento de dispositivos BLE (Kotlin) e debugging de ANR.*

Cada anti-padrão é anotado com **por que** é lento; cada correção, com o **mecanismo exato** que resolve. Este é um artefato de aprendizado, não um produto — clareza da lição vale mais que abstração.

📖 **Guia de State, Event, Effect e SRP:** [`docs/index.md`](docs/index.md)

---

## As três áreas

| Área | O quê | Onde |
| --- | --- | --- |
| **Listagens** | RecyclerView (`notifyDataSetChanged`, `RecycledViewPool`, `DiffUtil`...) e Jetpack Compose (`key`/`contentType`, estabilidade, `derivedStateOf`...), cada uma com uma tela de anti-padrão e uma tela corrigida | [`feature/listing/xml`](feature/listing/xml), [`feature/listing/compose`](feature/listing/compose) |
| **Bluetooth** | Clean Architecture completa (domain/data/presentation) em volta de `BluetoothGatt`: fila de operações GATT serializada, negociação de MTU, notificações via descriptor, tudo com Dagger | [`feature/bluetooth`](feature/bluetooth) |
| **ANR Lab** | 4 pares de botões — cada um trava a thread principal de um jeito diferente (`Thread.sleep`, loop de CPU, I/O de disco bloqueante, deadlock de dois locks) e mostra a correção ao lado | [`feature/anr`](feature/anr) |

## Estrutura dos módulos

```
app                        // Activity de entrada + navegação + componente Dagger + Firebase
build-logic                 // 3 convention plugins (application, library, compose)
core:common                 // Resource<T>, DispatcherProvider, gerador de dados fake
core:designsystem           // tema Compose compartilhado
feature:listing:compose     // LazyColumn: telas Unoptimized + Optimized
feature:listing:xml         // RecyclerView: telas Unoptimized + Optimized
feature:bluetooth:domain    // models, BleRepository (interface), use cases
feature:bluetooth:data      // scanner BLE + cliente GATT + módulo Dagger
feature:bluetooth:presentation // telas MVI
feature:anr                 // trigger + fix de cada anti-padrão de ANR
```

## Stack

Kotlin, Coroutines/Flow, Jetpack Compose e o Android View system (XML) lado a lado. DI com Dagger 2 puro (sem Hilt), wireado explicitamente em cada módulo que precisa — sem convention plugin escondendo isso, de propósito, para o setup de DI ficar visível para estudo. MVVM + MVI (State/Event/Effect selados) em toda camada de apresentação. Clean Architecture completa só onde faz sentido: a feature de Bluetooth.

## Firebase Crashlytics

Usado só para o ANR Lab poder mostrar o fluxo completo: forçar um ANR, deixar o processo registrar o motivo de saída, relançar o app, e conferir tanto o leitor local (`ActivityManager.getHistoricalProcessExitReasons`) quanto o console do Crashlytics.

---

## Documentação

O guia completo de **State**, **Event**, **Effect** e o **Princípio da Responsabilidade Única (SRP)** aplicado neste projeto está em [`docs/`](docs/index.md), organizado em capítulos e sempre linkando para o arquivo real do código quando possível.
