# SpawnResolver pour skip plateforme SKYBLOCK

- **Date :** 2026-08-18
- **Version début / fin :** 1.3.0-alpha.5 → 1.3.0-alpha.6

## Prompt / objectif

Île NBT OK ; la plateforme 3×3 SKYBLOCK de l’API reste. La désactiver via l’API, pas en cassant des blocs.

## Fait

- Logs post-fix : `ile1Copied=true`, templates `minecraft` + `personnalworld` présents.
- `DArchitectServices.registerSpawnResolver` : dims `personnalworld:*` → spawn île, `buildStarterPlatform` no-op (skip `SkyblockPlatformBuilder`).

## Fichiers touchés

- `src/main/java/fr/galsaxx/PersonnalWorld.java`

## Décisions

- Garder preset SKYBLOCK (pas de structures vanilla). Pas de flag builder `skipPlatform` en 0.0.44 : le SPI SpawnResolver est le hook officiel.

## Ouvert / à corriger

- Nouveau monde pour ne pas garder l’ancienne plateforme déjà posée.

## WIP

- 1.3.0-alpha.6 — plus de plateforme SKYBLOCK 3×3.
