# Baton sans GeckoLib — placements restaures

- **Date :** 2026-09-04
- **Version debut / fin :** 1.3.0-alpha.28 → 1.3.0-alpha.29

## Prompt / objectif

Remettre les display Blockbench fournis (`personnal_world_item_sansgeckolib.json`) a la place des transforms GeckoLib colles par erreur sur le modele JSON statique.

## Fait

- Import dans `personnal_world_item.json` et `_3d.json`
- Textures remappees vers `item/personnal_world_item_0|1`
- Copie reference sous `docs/reference/blockbench/`
- Pack GeckoLib non touche
