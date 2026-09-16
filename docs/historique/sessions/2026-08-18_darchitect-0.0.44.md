# DArchitect 0.0.44 — retrait contournement Tomlj

- **Date :** 2026-08-18
- **Version début / fin :** 1.3.0-alpha.3 → 1.3.0-alpha.4

## Prompt / objectif

Récupérer `dist/builds/0.0.44/darchitect-fabric.jar` (et NeoForge s’il est là) ; retirer le `tomlj` Maven si le jar est autonome sous Loom.

## Fait

- Copie `darchitect-fabric.jar` + `darchitect-neoforge.jar` 0.0.44 dans `libs/`.
- Vérif : classes `net/darchitect/shadow/tomlj/` et `…/antlr4/` présentes ; plus de nested jars ni `org/tomlj`.
- Retrait de `modImplementation("org.tomlj:tomlj:1.1.1")` Fabric/NeoForge.
- `depends` ≥ 0.0.44.

## Fichiers touchés

- `libs/darchitect-*.jar`
- `fabric/build.gradle.kts`, `neoforge/build.gradle.kts`
- `fabric.mod.json`, `neoforge.mods.toml`
- `gradle.properties`, `docs/CHANGELOG_WIP.md`, `docs/CAPABILITIES.md`, `README.md`

## Décisions

- 0.0.44 règle le P0 Loom `files()` ; plus de Tomlj côté consommateur.

## Ouvert / à corriger

- Valider `.\run-client.bat` jusqu’au menu (sans Tomlj Maven).

## WIP

- 1.3.0-alpha.4 — DArchitect 0.0.44, contournement Tomlj retiré.
