# Patch notes - 1.3.0

- **Canal :** official/stable (agregat depuis la derniere stable, sans doublons thematiques)
- **Depuis :** `1.2.2` (dernier canal stable)
- **Build (UTC) :** 2026-09-21T02:25:55.2730878Z
- **Git :** `1afc32d`
- **Capacites :** voir `docs/CAPABILITIES.md`
- **Source :** `docs/CHANGELOG_WIP.md` (Pending)

---

## Changements

- Config `personnalworld.toml` : `noDimensionSavePosition`, `noDimensionTeleport`, `staffCooldownTicks`, `shareInventory` (commentaires EN + exemples).
- Première release Fabric stable (ligne 1.3 DArchitect ; NeoForge reporté).
- Retour : save depuis toute dim hors perso (sauf denylist) ; sans NBT → spawn joueur puis Overworld ; messages traduits ; plus de `StructureCopier` ; île `personnalworld:ile_1` puis classpath.
- Première **beta** publique Fabric (retours joueurs) : contenu alpha.32, notes EN storefront.
- Client Fabric : fix compile `modifyModelOnLoad` (`resourceId` / `topLevelId`, plus `id()`).
- Bâton + GeckoLib : displays calibrés `personnal_world_item_geckolib` (espace `builtin/entity`) ; JSON classique inchangé sans GeckoLib.
- `spawn_marker` : tuile **32×32** haut-gauche par frame (32×480, source 128).
- TP monde perso : toujours pieds à solY+1 (sol naturel ou bedrock) ; scan inclut le Y du spawn ; vérif espace libre aux pieds.
- TP monde perso : scan 50 blocs sous le spawn ; si sol trouvé → TP à Y+1 ; sinon bedrock à spawnY−3 et TP au-dessus.
- Bâton : nouveaux placements main 1P/3P, GUI et fixed (export Blockbench).
- Bâton : anim idle — le cube gem tourne sur lui-même (pivot centre) avec un très léger va-et-vient Y en boucle ; placements main/GUI alignés sur le JSON classique.
- Spawn île en 0 90 0 (NBT décalé d’autant).
- Plus de plateforme SKYBLOCK 3×3 : spawn via `SpawnResolver` API, l’île NBT suffit.
- L’île NBT se charge depuis le classpath (plus d’erreur « île introuvable » en dev). Mondes perso sans structures vanilla (preset SKYBLOCK).
- Breaking : les anciens mondes DimLib ne sont pas migrés.

