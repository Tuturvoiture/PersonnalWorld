# P3 — Pas de flag builder pour skip la plateforme SKYBLOCK/VOID

- **Date :** 2026-08-18
- **API visée :** 0.0.44
- **Sévérité :** P3 — le SPI `SpawnResolver` permet de skip, mais il n’y a pas `DimensionBuilder.starterPlatform(false)`
- **Statut PW :** contourné via `DArchitectServices.registerSpawnResolver` (no-op platform)

SKYBLOCK appelle toujours `SkyblockPlatformBuilder.ensurePlatform` sauf si `SpawnResolver.resolve()` retourne un point (alors `buildStarterPlatform` skip le built-in).

Souhait consommateur : monde VOID/SKYBLOCK **sans** 3×3, l’île NBT du mod suffit.

Correctif API optionnel : `DimensionBuilder.starterPlatform(boolean)` par dimension, sans SPI global (le resolver est un singleton qui remplace tout autre).
