# Import geo Blockbench GeckoLib

- **Date :** 2026-08-20
- **Version début / fin :** 1.3.0-alpha.11 → 1.3.0-alpha.12

## Prompt / objectif

Intégrer l’export Blockbench GeckoLib fourni (`personnal_world_item_geckolib.bbmodel` + `.geo.json`) pour retrouver la vraie forme / texture du bâton.

## Fait

- Texture 48×48 extraite du `.bbmodel` (`cber1.png` embarquée) → `textures/item/personnal_world_item.png`.
- Geo remplacé par l’export (cubes / UV / rotations), avec os `staff` + `gem` pour les anims idle/use.
- Copies de référence dans `docs/reference/blockbench/`.

## Fichiers touchés

- `assets/.../geo/item/personnal_world_item.geo.json`
- `assets/.../textures/item/personnal_world_item.png`
- `docs/reference/blockbench/*`

## Décisions

- Pas d’animation dans le `.bbmodel` : on garde `idle` / `use` existants.
- Seule adaptation : scinder le cube d’herbe en bone `gem`.

## Ouvert / à corriger

- Vérifier en jeu orientation + idle du cube.
- **Anims provisoires** : garder `idle` / `use` pour l’instant ; plus tard les remplacer par un export Blockbench (Animate → `.animation.json`) et aligner les noms dans `PersonnalWorldGeoItem` si besoin.

## WIP

- 1.3.0-alpha.12 — import Blockbench GeckoLib.
