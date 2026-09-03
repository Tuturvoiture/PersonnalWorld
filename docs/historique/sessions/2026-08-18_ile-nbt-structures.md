# Île NBT classpath + sans structures vanilla

- **Date :** 2026-08-18
- **Version début / fin :** 1.3.0-alpha.4 → 1.3.0-alpha.5

## Prompt / objectif

Île NBT introuvable ; structures vanilla dans le monde perso.

## Fait

- Logs : `StructureCopier` CWD `fabric/run`, `isJar=false`, `ile1Copied=false` ; templates `minecraft:` et `personnalworld:` absents.
- Copie NBT via classpath ; pose île depuis le stream NBT.
- Contournement structures : preset SKYBLOCK (VOID API laisse `Optional.empty()` = structures on).
- Rapport API `2026-08-18_void-structures.md`.

## Fichiers touchés

- `StructureCopier.java`, `IslandGenerator.java`, `PersonnalWorldUtil.java`
- `data/personnalworld/structure/*.nbt`

## Décisions

- Île = bug PW. Structures vanilla = API VOID, contourné SKYBLOCK.

## Ouvert / à corriger

- Vérifier nouveau monde : île NBT + pas de villages.

## WIP

- 1.3.0-alpha.5 — île NBT classpath ; mondes perso sans structures vanilla.
