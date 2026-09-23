# Invitations & droits îles

- **Date :** 2026-09-16
- **Version début / fin :** 1.3.1 → 1.4.0-alpha.0

## Prompt / objectif

Mettre en place invitations et droits non-owner : commandes `/pw`, whitelist JSON hors îles (live + anti-corruption), visiteurs temporaires, debug setowner/reload, préparation API UI. BANNED en code sans application.

## Fait

- Couche `fr.galsaxx.invite` + `AccessFileStore` / validator / quarantine
- Création dim `MANAGED` + owner ; migration soft access
- `PresenceAndRightsGuard` (purge TEMP, expulsion sans JOIN)
- Commandes joueur + debug ops
- Config TOML `allowPassiveIslandVisit` / `enableDebugCommands`
- Stub `SyncIslandMembersPayload` + `IslandMembersApi`

## Fichiers touchés

- `src/main/java/fr/galsaxx/invite/*`, `command/PersonnalWorldCommand.java`, `network/SyncIslandMembersPayload.java`
- `PersonnalWorldUtil.java`, `PersonnalWorldContent.java`, `PersonnalWorldConfig.java`
- Docs CAPABILITIES / COMMANDS / HORS_SCOPE / BDD / WIP

## Décisions

- PW JSON = source de vérité ; DA AccessManager wrap `impl`
- BANNED reporté (rappel HORS_SCOPE)

## Ouvert / à corriger

- Smoke multi-joueurs ; unload DA best-effort ; UI livre

## WIP

- 1.4.0-alpha.0 — invitations / droits îles (voir CHANGELOG_WIP)
