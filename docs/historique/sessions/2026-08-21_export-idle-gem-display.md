# Export idle gem + display GeckoLib

- **Date :** 2026-08-21
- **Version début / fin :** 1.3.0-alpha.12 → 1.3.0-alpha.13

## Prompt / objectif

Exporter depuis Blockbench (MCP) le modèle GeckoLib + anim idle (cube qui tourne + léger bob Y) et appliquer les placements display retunés dans les dossiers du mod.

## Fait

- Export geo `staff` / `gem` (pivot gem au centre `[0,16,0]`).
- Anim `idle` (4 s, loop) : rotation Y continue + position Y ±0.15 ; `use` conservée.
- Display main/GUI/etc. pris depuis le projet GeckoLib Blockbench (pas l’ancien JSON Java seul).

## Fichiers touchés

- `assets/.../geo/item/personnal_world_item.geo.json`
- `assets/.../animations/item/personnal_world_item.animation.json`
- `assets/.../models/item/personnal_world_item.json`
- `assets/.../models/item/personnal_world_item_3d.json`
- `resourcepacks/geckolib_staff/.../personnal_world_item.json`
- `PersonnalWorldGeoItem.java` (commentaire)

## Décisions

- Display = valeurs actuelles du projet GeckoLib (espace geo), pas une copie brute du backup Java.

## Ouvert / à corriger

- Vérifier en jeu main 1P/3P + GUI avec GeckoLib chargé.

## WIP

- 1.3.0-alpha.13 — Bâton : anim idle gem + placements alignés export Blockbench.
