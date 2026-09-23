# Patch notes - 1.4.0-beta.0

- **Canal :** beta (agregat depuis la derniere beta, sans doublons thematiques)
- **Depuis :** `1.3.0-beta.0` (dernier canal beta)
- **Build (UTC) :** 2026-09-21T02:25:55.2730878Z
- **Git :** `1afc32d`
- **Capacites :** voir `docs/CAPABILITIES.md`
- **Source :** `docs/CHANGELOG_WIP.md` (Pending)

---

## Changements

- Première **beta** ligne 1.4 (invitations / droits îles, sync DA ≥ 0.1.2, cibles online/offline, façade UI prête).
- (build intermédiaire) alignement docs / jar avant passage beta.
- Façade UI `IslandMembersApi` (mutations + sync S2C Architectury) ; cache client pour le futur livre.
- Fix liaison visit TEMP : `applyRecord` ne wipe plus le GUEST DA ; `/pw` résout nom/UUID via `PlayerRef` (offline fiable).
- (corrigé) doc hors-scope offline — annulé par alpha.3.
- DArchitect **≥ 0.1.2** : sync rôles atomique (`access()` / `setRolesForDimension` / `clearRole`) ; migration soft MANAGED+owner ; purge TEMP propre.
- Sortie publique Fabric : config `personnalworld.toml` documentée ; README/notes EN ; nettoyage jar (stubs, pack GeckoLib obsolète, junk textures) ; depends `fabric-api`/`architectury` resserrées.
- Retour : save depuis toute dim hors perso (sauf denylist) ; sans NBT → spawn joueur puis Overworld ; messages traduits ; plus de `StructureCopier` ; île `personnalworld:ile_1` puis classpath.
- Config `personnalworld.toml` : `noDimensionSavePosition`, `noDimensionTeleport`, `staffCooldownTicks`, `shareInventory` (commentaires EN + exemples).

