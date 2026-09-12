# P2 — WorldType.VOID génère encore les structures vanilla

- **Date :** 2026-08-18
- **Consommateur :** PersonnalWorld `1.3.0-alpha.5`
- **API visée :** DimensionArchitect **0.0.44**
- **Sévérité :** P2 — un monde `VOID` n’est pas vide de structures (villages, etc.)
- **Statut côté PW :** contourné via `WorldPreset.SKYBLOCK` + `StructureGenerationRules.none()`

## Repro

`api.builder(id).type(WorldType.VOID).build()` puis explorer la dimension : structures vanilla possibles.

## Cause

`DimensionManagerImpl.createVoidServerWorld` :

```java
new FlatChunkGeneratorConfig(Optional.empty(), defaultBiome, List.of())
```

En Minecraft, `Optional.empty()` = **overrides absents = structures par défaut du biome**.  
SKYBLOCK fait déjà le bon appel : `Optional.of(RegistryEntryList.of())` (liste vide = aucune structure).

## Correctif attendu (API)

Dans `createVoidServerWorld` (et `createFlatServerWorld` si le plat doit pouvoir être sans structures) : même chose que `StructureGenerationOps.skyblockConfig` — `Optional.of(RegistryEntryList.of())` pour VOID.

Vérif : GameTest VOID → aucun `StructureStart` dans les chunks autour du spawn.

## Contournement PersonnalWorld

`worldProfile(SKYBLOCK + structures none())` — le profil **prime** sur `WorldType.VOID` pour le générateur.
