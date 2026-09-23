# Merge invites → livre (alpha.18)

## Objectif

Ramener invitations / droits îles dans `feature/livre-interface-iles` comme base du gros projet GUI.

## Fait

- Merge `cursor/island-invites-permissions-73e0` (jusqu’à `1.4.0-beta.1`) dans la branche carnet.
- Conflits : `PersonnalWorldContent` (carnet + `/pw` + sync), version → `1.4.0-alpha.18`, docs WIP / COMMANDS / état courant.
- DA ≥ 0.1.2, package `invite/`, façade `IslandMembersApi` présents à côté du carnet.

## Pointeurs

- `src/main/java/fr/galsaxx/invite/`
- `IslandMembersApi`, `SyncIslandMembersPayload`
- `docs/CAPABILITIES.md`, `docs/COMMANDS.md`

## Suite

- UI carnet branchée sur membres / permissions réels.
