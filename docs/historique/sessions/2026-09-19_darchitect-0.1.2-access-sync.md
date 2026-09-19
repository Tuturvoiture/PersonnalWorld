# DA 0.1.2 + sync droits + offline

- **Date :** 2026-09-19
- **Version début / fin :** 1.4.0-alpha.0 → 1.4.0-alpha.1

## Prompt / objectif

Intégrer DimensionArchitect 0.1.2 (`access()` public), corriger la sync kick/TEMP, permettre kick/visit offline, documenter.

## Fait

- Jars `libs/` 0.1.2 ; depends `>=0.1.2`
- `DArchitectAccess` sans `impl` : `setRolesForDimension`, `clearRole`, `migrateManaged`
- Kick / leave / purge TEMP sans rôles DA fantômes
- `GameProfileArgumentType` pour invite/kick/role/visit (offline)
- Docs COMMANDS / CAPABILITIES / rapport API / BDD / WIP

## Fichiers touchés

- `libs/darchitect-*.jar`, `invite/DArchitectAccess.java`, `PlayerRef.java`, `IslandAccessService.java`, `PersonnalWorldCommand.java`, `PersonnalWorldUtil.java`, docs…

## Décisions

- Option A confirmée avec API 0.1.2
- Offline sans attendre DA 0.1.3
- Unload public = plus tard

## Ouvert / à corriger

- Smoke multi-joueurs en jeu
- `reload-island` unload natif (DA 0.1.3)

## WIP

- 1.4.0-alpha.1 — voir CHANGELOG_WIP
