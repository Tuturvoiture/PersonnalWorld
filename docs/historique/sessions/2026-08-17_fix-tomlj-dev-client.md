# Fix crash Tomlj au run client

- **Date :** 2026-08-17
- **Version début / fin :** 1.3.0-alpha.0 → 1.3.0-alpha.1

## Prompt / objectif

Le client Fabric crash au démarrage : `NoClassDefFoundError: org/tomlj/TomlInvalidTypeException` dans l’entrypoint `darchitect`.

## Fait

- Cause : le jar local `libs/darchitect-fabric.jar` n’embarque pas `tomlj` (dépendance `implementation` non incluse dans le shadow DArchitect).
- Ajout de `modImplementation("org.tomlj:tomlj:1.1.1")` dans `fabric/build.gradle.kts` et `neoforge/build.gradle.kts`.

## Fichiers touchés

- `fabric/build.gradle.kts`
- `neoforge/build.gradle.kts`
- `gradle.properties`
- `docs/CHANGELOG_WIP.md`

## Décisions

- Correctif côté PersonnalWorld pour le dev local ; idéalement DArchitect devrait shadow `tomlj` dans son jar release.

## Ouvert / à corriger

- Valider `run-client.bat` jusqu’au menu principal et gameplay.
- Rapport API : [`../api-darchitect/2026-08-17_tomlj-runtime-jar.md`](../api-darchitect/2026-08-17_tomlj-runtime-jar.md) (à corriger dans DimensionArchitectAPI).

## WIP

- 1.3.0-alpha.1 — Dev client : dépendance `tomlj` pour DimensionArchitect avec jar local.
