# Vérif intégrale depuis 1.3.1 → 1.4.0-beta.7

**Date :** 2026-10-01  
**Méthode :** audit doc ↔ code sur les sources `1.4.0-beta.7` + présence du jar `builds/1.4.0-beta.7/1.4.0-beta.7-fabric.jar`.  
**Smoke in-game / duo :** non exécuté dans cette session → les cases UI/anim/duo sont **OK (code)** si le chemin est branché ; **N/A (runtime)** si seule une partie live peut trancher le ressenti.

Légende : `OK` = présent et cohérent | `KO` = trou ou contradiction | `N/A` = non testable ici / hors périmètre.

## Audit

| Id | Résultat | Note |
|----|----------|------|
| audit-wip-curseforge | OK | WIP couvre alpha.0→beta.7 ; jar beta.7 présent. **Écart doc :** `CURSEFORGE_EN` beta.2 dit encore créer Forêt/Roche — obsolète depuis beta.3 (presets locked). |
| audit-island-ui-commands | OK | `ISLAND_UI.md` / `COMMANDS.md` alignés avec `AdventureBookScreen`, `IslandBookNetworking`, `PersonnalWorldCommand`. |
| audit-guards-da-presets-anim | OK | `IslandRoleGuard`, `IslandPvpGuard` (+ mixin `attack`), `DArchitectQuotaSync`, `IslandPresetRegistry`, `AdventureBookClientPose.beginClosing` / `allowCoverAnim`. |

## A — Régression 1.3.1

| Id | Résultat | Note |
|----|----------|------|
| a1-staff-island-tp | OK | `PersonnalWorldItem` : île active, `ensurePersonalWorld`, TP spawn sûr. |
| a2-returnworld | OK | `/returnworld` + `ReturnTeleport` (inchangé ligne 1.3). |
| a3-spawn-marker-toml | OK | `SPAWN_MARKER` + `PersonnalWorldConfig` / clés documentées. |
| a4-sans-geckolib | OK | `GeckoLibHooks.createBookItem` / bâton via `Class.forName` → fallback item statique. |

## B — Carnet 3D

| Id | Résultat | Note |
|----|----------|------|
| b1-open-gui-close | OK | `use` → OPEN + délai GUI ; `notifyLeave` → close payload. |
| b2-close-no-flash-hop | OK (code) / N/A (runtime) | Phase `closing` hors NBT (beta.7). Ressenti flash/hop = smoke live. |
| b3-anim-isolee-duo | OK (code) / N/A (runtime) | Instance par `renderHolder` + `BookReadingPayload` ; duo live non joué. |

## C — GUI îles

| Id | Résultat | Note |
|----|----------|------|
| c1-tabs-pagination-plafond | OK | Onglets OWNED/INVITED ; `IslandPageLayout` ; Créer désactivé si `owned.size() >= maxIslands`. |
| c2-presets-ligne-bientot | OK | Grille 1×N `sidePad`/`step` ; locked + `locked_overlay` ; 4 défauts registry. |
| c3-reglages-owner | OK | Rename, spawn move, terrain/feu/PvP ; météo non branchée (hors scope). |
| c4-iles-invitees-rejoindre | OK | `ownerName` + rôle ; join si whitelist + dim existante (`IslandBookNetworking`). |

## D — Multi-îles

| Id | Résultat | Note |
|----|----------|------|
| d1-deuxieme-ile-ids | OK | `IslandIds` / lifecycle slots `perso_<uuid>_<n>`. |
| d2-plafond-max-iles | OK | Config + UI + `IslandLifecycle.create` / `/pw create`. |
| d3-ile-active-monde1 | OK | Bâton lit `active` ; `IslandDefaultName` Monde 1 / World 1. |

## E — Invitations & `/pw`

| Id | Résultat | Note |
|----|----------|------|
| e1-livre-invites-roles | OK | ADD_INVITE online + champ libre ; `flushRoleEdits` 1 msg ; kick UUID. |
| e2-cocreateur-invites | OK | `canManageMembers` / `IslandBookNetworking` actions membres. |
| e3-pw-commandes | OK | invite/kick/role/list/visit/leave/create ; `leave_owner` si owner. |
| e4-sync-deux-carnets | OK | `IslandMembersApi.syncBooks` → `sendSync` acteur + cible online. |
| e5-fichiers-access | OK | `AccessFileStore` sous `personnalworld/access/`. |

## F — Visiteur

| Id | Résultat | Note |
|----|----------|------|
| f1-visiteur-restrictions | OK | BREAK/PLACE/coffres + LIVING_HURT + `attack` mixin. |
| f2-builder-cocreateur | OK | `visitor()` faux pour BUILDER / CO_CREATOR / OWNER. |
| f3-baton-chez-hote | OK | `ownsCurrentIsland` faux → `forgetIfDimension` + `teleportHome`. |
| f4-rejoindre-messages | OK | `ReturnTeleport.rememberIfAllowed` ; action bar ; invite tchat (AccessService). |

## G — Gamerules & DA

| Id | Résultat | Note |
|----|----------|------|
| g1-gamerules-pvp-ile | OK | `IslandGameruleSync` + overlay `pvp` dans `IslandPvpGuard`. |
| g2-da-max-simultaneous | OK | `DArchitectQuotaSync.applyIfEnabled` au boot (`PersonnalWorldContent`). |

## H — Docs

| Id | Résultat | Note |
|----|----------|------|
| h1-capabilities | OK | Mis à jour → `1.4.0-beta.7` + carnet / presets / DA. |
| h2-hors-scope | OK | Ligne « pas de menu livre » retirée ; météo / presets locked / BANNED clarifiés. |

## Synthèse

- **Aucun KO code** sur le delta 1.3.1 → beta.7.
- **Dettes doc mineures :** republier / régénérer notes EN storefront (CURSEFORGE) pour refléter presets verrouillés + beta.7.
- **Smoke live recommandé :** B2, B3, E/F duo (ressenti anim + droits en jeu).

## Fichiers touchés par cette vérif

- [`docs/CAPABILITIES.md`](../CAPABILITIES.md)
- [`docs/HORS_SCOPE.md`](../HORS_SCOPE.md)
- ce rapport
