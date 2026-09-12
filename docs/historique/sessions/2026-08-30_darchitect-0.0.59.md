# DArchitect 0.0.59 — inventaire partagé

- **Date :** 2026-08-30
- **Version début / fin :** 1.3.0-alpha.15 → 1.3.0-alpha.16

## Prompt / objectif

Passer à DArchitect 0.0.59 (`isolate_player_data=false` par défaut) ; mondes perso PW gardent l’inventaire partagé entre dimensions.

## Fait

- Jars 0.0.59 dans `libs/` ; PATCHNOTES copié.
- `depends` ≥ 0.0.59.
- `PersonnalWorldUtil` : `.isolatePlayerData(false)` à la création de `personnalworld:perso_*`.
- Docs courants + BDD + décision durable.

## Fichiers touchés

- `libs/darchitect-*.jar`
- `src/main/java/fr/galsaxx/util/PersonnalWorldUtil.java`
- `fabric.mod.json`, `neoforge.mods.toml`, `gradle.properties`
- `docs/CHANGELOG_WIP.md`, `README.md`, `docs/ENVIRONMENTS.md`, `docs/CAPABILITIES.md`
- `docs/historique/api-darchitect/`, `docs/historique/bdd/`

## Décisions

- Politique PW : inventaire partagé, figé par dim à la création (override explicite `false`).

## Ouvert / à corriger

- Mondes perso déjà créés sous 0.0.57 (isolation par défaut) : supprimer/recréer la dim si l’inventaire reste isolé.

## WIP

- 1.3.0-alpha.16 — DArchitect 0.0.59 + inventaire partagé.
