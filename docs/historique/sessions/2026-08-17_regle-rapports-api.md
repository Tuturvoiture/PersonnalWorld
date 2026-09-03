# Règle rapports API DArchitect

- **Date :** 2026-08-17
- **Version début / fin :** 1.3.0-alpha.1 (inchangée — process / docs)

## Prompt / objectif

Si PersonnalWorld bute sur un problème DimensionArchitect, produire un rapport que le mainteneur peut corriger dans le Cursor de l’API ; l’ancrer en règle Cursor.

## Fait

- Règle `.cursor/rules/darchitect-api-reports.mdc`
- Premier rapport : jar 0.0.42 sans Tomlj (crash init)

## Fichiers touchés

- `.cursor/rules/darchitect-api-reports.mdc`
- `docs/historique/api-darchitect/`

## Décisions

- Ne pas patcher DimensionArchitect depuis ce workspace.
- Contournement PW temporaire OK si le run est bloqué ; le vrai fix est l’API.

## Ouvert / à corriger

- Coller le rapport Tomlj dans le Cursor DimensionArchitectAPI et republier le jar.

## WIP

- (aucun — pas de changement joueur)
