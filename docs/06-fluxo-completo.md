# Como as três peças se conectam

*O fluxo completo, de clique a recomposição*

```
 1. Usuário age          2. Processa              3a. Atualiza (persiste)
    [ Event ]  ────────▶  [ ViewModel.onEvent() ]  ────────▶  [ State ]
                                    │
                                    └────────────▶  [ Effect ]
                                         3b. Emite (uma vez)
```

Na UI (Compose), as duas pontas são observadas de formas diferentes:

```kotlin
// State: sempre observado, sempre reflete o valor mais recente
val state by viewModel.state.collectAsStateWithLifecycle()

// Effect: observado com LaunchedEffect, que reinicia a coleta só quando
// a chave (Unit, aqui) muda -- cada emissão do SharedFlow é entregue uma
// única vez para quem estiver coletando naquele instante
LaunchedEffect(Unit) {
    viewModel.effects.collect { effect ->
        when (effect) {
            is BluetoothEffect.ShowError -> /* mostrar snackbar */
        }
    }
}
```

Veja isso funcionando de ponta a ponta, com tratamento de permissões incluído, em
[`BluetoothScreen.kt`](../feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothScreen.kt).

---

[↑ Índice](./index.md) · [Anterior: Effect](./05-effect.md) · [Próximo: ANR Lab, os 4 pares lado a lado →](./07-anr-lab-pares.md)
