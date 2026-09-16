# Geo bâton aligné sur Blockbench

- **Date :** 2026-08-20
- **Version début / fin :** 1.3.0-alpha.10 → 1.3.0-alpha.11

## Prompt / objectif

Animations OK, mais forme et textures du bâton GeckoLib ne ressemblaient plus au modèle Blockbench d’origine.

## Fait

- Atlas `personnal_world_item.png` reconstruit en 96×48 (`#0` gauche, `#1` droite).
- `.geo.json` régénéré depuis les cubes / UV / rotations du JSON Blockbench (pivots Bedrock, os pour les angles).

## Fichiers touchés

- `assets/.../geo/item/personnal_world_item.geo.json`
- `assets/.../textures/item/personnal_world_item.png`

## Décisions

- Garder les anims idle/use sur `staff` + `gem`.

## Ouvert / à corriger

- Si l’écart reste visible : réexporter Blockbench → GeckoLib (plugin) pour une correspondance pixel-perfect.

## WIP

- 1.3.0-alpha.11 — forme/textures bâton réalignées.
