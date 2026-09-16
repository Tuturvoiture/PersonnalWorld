# Tomlj rétabli pour Loom runClient

- **Date :** 2026-08-17
- **Version début / fin :** 1.3.0-alpha.2 → 1.3.0-alpha.3

## Prompt / objectif

Crash client après DArchitect 0.0.43 : même `NoClassDefFoundError: org/tomlj/TomlInvalidTypeException`.

## Fait

- 0.0.43 embarque bien Tomlj en nested jars + `"jars"` dans `fabric.mod.json`.
- Loom `modImplementation(files(...))` ne charge pas ces nested jars au `runClient`.
- Contournement `tomlj` rétabli ; rapport API : `docs/historique/api-darchitect/2026-08-17_tomlj-loom-nested-jars.md`.

## Fichiers touchés

- `fabric/build.gradle.kts`, `neoforge/build.gradle.kts`
- `gradle.properties`, `docs/CHANGELOG_WIP.md`

## Décisions

- Garder 0.0.43 (prod OK) ; Tomlj Maven seulement pour le dev Loom.

## Ouvert / à corriger

- Relancer `.\run-client.bat`.
- Côté API : shadow Tomlj dans le jar ou smoke `files()` + runClient.

## WIP

- 1.3.0-alpha.3 — `tomlj` rétabli pour Loom.
