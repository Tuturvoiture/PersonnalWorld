# Bloc spawn_marker

- **Date :** 2026-08-30
- **Version début / fin :** 1.3.0-alpha.18 → 1.3.0-alpha.19

## Prompt / objectif

Bloc d’apparition Personnal World à Y=90 : incassable, sans item/créatif/recette ; référence de spawn par dimension ; bedrock Y=87 conservé en secours.

## Fait

- `PersonalSpawnMarkerBlock` + enregistrement `spawn_marker` (pas de BlockItem).
- Assets animés Blockbench ; `PersonalWorldSpawnReference` + NBT `PWWorldState`.
- TP prioritaire marqueur Y+1 ; fallback scan + bedrock inchangé.

## Fichiers touchés

- `src/main/java/fr/galsaxx/block/PersonalSpawnMarkerBlock.java`
- `PersonnalWorldContent.java`, `PersonnalWorldUtil.java`
- `PersonalWorldSpawnReference.java`, `PersonalWorldSpawnSafety.java`
- `assets/personnalworld/` (block, textures, lang)
- `docs/reference/blockbench/PersonnalWorld_Block_Spawn.json`

## Décisions

- Marqueur Y=90 ; bedrock Y=87 = secours ; `islandProfile` stub pour futures îles.

## Ouvert / à corriger

- Test en jeu : marqueur visible, TP Y=91, bedrock si void.

## WIP

- 1.3.0-alpha.19 — Bloc spawn_marker.
