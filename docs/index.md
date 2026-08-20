---
layout: home
title: "Android Fundamentals Lab"
nav_order: 1
---

# Android Fundamentals Lab

*Anti-padrões de performance vs. correções, usando o código real deste projeto — RecyclerView, Compose, Bluetooth (BLE) e ANR.*

📦 **Repositório:** [github.com/LeonardoBai12/BluetoothAndListingOptimization](https://github.com/LeonardoBai12/BluetoothAndListingOptimization)

Todo trecho de código citado nestas páginas existe de verdade no projeto — sempre que possível, o link do arquivo real vem junto, direto para o GitHub, então dá para abrir o arquivo inteiro em vez de confiar só no trecho colado aqui. Para conceitos gerais de arquitetura (SOLID, MVI, State/Event/Effect), veja o
[Advanced Kotlin & Android Engineering](https://leonardobai12.github.io/AdvancedKotlinAndroidEngineering/) — este guia fica focado só no que é específico deste projeto.

## Capítulos

1. [RecyclerView](./01-recyclerview/) — `DiffUtil`, `setIsRecyclable`, `RecycledViewPool` compartilhado, IDs estáveis
2. [Compose (LazyColumn)](./02-compose-lazycolumn/) — estabilidade, `key`/`contentType`, `remember`/`derivedStateOf`
3. [Bluetooth (GATT)](./03-ble-gatt/) — a fila de operações GATT serializada, negociação de MTU
4. [ANR Lab](./04-anr-lab/) — os 4 pares problema/solução
