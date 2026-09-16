# DArchitect 0.0.57

- **Date :** 2026-08-30
- **Version début / fin :** 1.3.0-alpha.14 → 1.3.0-alpha.15

## Prompt / objectif

Mettre à jour PersonnalWorld sur DimensionArchitect 0.0.57 (jars + dépendance) ; conserver tout l’historique ; pas de reset versionning.

## Fait

- Copie `darchitect-fabric.jar` + `darchitect-neoforge.jar` 0.0.57 dans `libs/`.
- Copie `PATCHNOTES-0.0.57.txt` sous `docs/historique/api-darchitect/`.
- `depends` ≥ 0.0.57 (Fabric / NeoForge).
- Catch `DimensionAlreadyExistsException` dans `PersonnalWorldUtil` (étend `RuntimeException`).
- Compile Fabric + NeoForge OK.
- Pointeurs courants : README, ENVIRONMENTS, CAPABILITIES, BDD.

## Fichiers touchés

- `libs/darchitect-*.jar`
- `fabric.mod.json`, `neoforge.mods.toml`
- `src/main/java/fr/galsaxx/util/PersonnalWorldUtil.java`
- `gradle.properties`, `docs/CHANGELOG_WIP.md`
- `README.md`, `docs/ENVIRONMENTS.md`, `docs/CAPABILITIES.md`
- `docs/historique/api-darchitect/`, `docs/historique/bdd/etat-courant.md`

## Décisions

- Pas de purge d’historique ; floor API = 0.0.57 pour les nouvelles installs.

## Ouvert / à corriger

- Smoke `runClient` avec jars 0.0.57.

## WIP

- 1.3.0-alpha.15 — DArchitect 0.0.57.
