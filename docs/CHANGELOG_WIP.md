# Changelog WIP

Journal vivant des changements fonctionnels **non encore archivés** dans [`CHANGELOG.md`](CHANGELOG.md).

**Dernière version en ligne :** aucune

## Pending

- 1.3.0-beta.0 — Première **beta** publique Fabric (retours joueurs) : contenu alpha.32, notes EN storefront.
- 1.3.0-alpha.32 — Client Fabric : fix compile `modifyModelOnLoad` (`resourceId` / `topLevelId`, plus `id()`).
- 1.3.0-alpha.31 — Bâton + GeckoLib : displays calibrés `personnal_world_item_geckolib` (espace `builtin/entity`) ; JSON classique inchangé sans GeckoLib.
- 1.3.0-alpha.30 — Bâton + GeckoLib : un seul placement (JSON classique) ; le wrapper ne force plus un second JSON display — animation geo conservée.
- 1.3.0-alpha.29 — Bâton sans GeckoLib : placements Display restaurés depuis `personnal_world_item_sansgeckolib.json` (mains / GUI / sol / fixed).
- 1.3.0-alpha.28 — `spawn_marker` : tuile **32×32** haut-gauche par frame (32×480, source 128).
- 1.3.0-alpha.27 — `spawn_marker` : PNG recadré strict 16×240 (tuile **haut-gauche** par frame) + `cube_all` sans UV inversées.
- 1.3.0-alpha.26 — `spawn_marker` : UV BB `[0,0,16,16]` + `texture_size` 64 (1 tuile 16×16 étirée sur la face, plus de grille 2×2).
- 1.3.0-alpha.25 — `spawn_marker` : texture recadrée 64×960 (15 frames, quadrant BB) + `cube_all` ; glow centré comme Blockbench.
- 1.3.0-alpha.24 — `spawn_marker` : `texture_size` 64×64 (grille BB) + UV `[0,0,16,16]` non inversées sur toutes les faces.
- 1.3.0-alpha.23 — `spawn_marker` : UV recadrées sur le coin haut-gauche 16×16 de chaque frame (`texture_size` 128).
- 1.3.0-alpha.22 — `spawn_marker` : texture 128×1920 (15 frames) extraite du `.bbmodel` ; modèle `cube_all` ; `frametime` 3 (~7 fps Blockbench).
- 1.3.0-alpha.21 — Fix texture `spawn_marker` : PNG 64×1024 (16 frames) + modèle `cube_all` (UV Blockbench invalides → bloc noir).
- 1.3.0-alpha.20 — `spawn_marker` à **Y=88** (pieds Y=89) ; texture Blockbench mise à jour.
- 1.3.0-alpha.19 — Bloc `spawn_marker` (incassable, sans item/créatif) : référence de spawn par dim ; bedrock Y=87 conservé en secours.
- 1.3.0-alpha.18 — TP monde perso : toujours pieds à solY+1 (sol naturel ou bedrock) ; scan inclut le Y du spawn ; vérif espace libre aux pieds.
- 1.3.0-alpha.17 — TP monde perso : scan 50 blocs sous le spawn ; si sol trouvé → TP à Y+1 ; sinon bedrock à spawnY−3 et TP au-dessus.
- 1.3.0-alpha.16 — DArchitect **0.0.59** ; inventaire **partagé** entre dims (`isolatePlayerData(false)` à la création).
- 1.3.0-alpha.15 — DimensionArchitect **0.0.57** (jars `libs/`) ; dépendance runtime `>= 0.0.57` ; catch `DimensionAlreadyExistsException` à la création de dim.
- 1.3.0-alpha.14 — Bâton : nouveaux placements main 1P/3P, GUI et fixed (export Blockbench).
- 1.3.0-alpha.13 — Bâton : anim idle — le cube gem tourne sur lui-même (pivot centre) avec un très léger va-et-vient Y en boucle ; placements main/GUI alignés sur le JSON classique.
- 1.3.0-alpha.12 — Bâton : modèle / texture GeckoLib pris depuis l’export Blockbench (forme et UV d’origine).
- 1.3.0-alpha.11 — Bâton GeckoLib : forme et textures réalignées sur le modèle Blockbench d’origine (atlas + pivots).
- 1.3.0-alpha.10 — Bâton : le rendu GeckoLib est vraiment utilisé (plus le JSON statique). Idle et clic-droit s’affichent si GeckoLib 4.7.x est chargé.
- 1.3.0-alpha.9 — Bâton : animations GeckoLib (idle + impulsion au clic-droit) si GeckoLib est installé ; sans GeckoLib le modèle 3D statique reste, pas de crash.
- 1.3.0-alpha.8 — Item, modèle 3D, recette, île NBT et `/returnworld` sont dans le module commun : même gameplay sur Fabric et NeoForge.
- 1.3.0-alpha.0 — Les mondes perso sont créés via DimensionArchitect (dimension vide) : DimLib n’est plus requis.
- 1.3.0-alpha.0 — Breaking : les anciens mondes DimLib ne sont pas migrés.
- 1.3.0-alpha.1 — Dev client : dépendance `tomlj` ajoutée pour que DimensionArchitect démarre avec le jar local.
- 1.3.0-alpha.2 — Mise à jour DArchitect 0.0.43 (Tomlj embarqué) ; retrait du contournement tomlj côté PW.
- 1.3.0-alpha.3 — Dev client : `tomlj` rétabli — Loom ne charge pas les nested jars du jar DArchitect 0.0.43.
- 1.3.0-alpha.4 — DArchitect 0.0.44 (Tomlj shadowé) : plus besoin de `tomlj` côté PersonnalWorld.
- 1.3.0-alpha.5 — L’île NBT se charge depuis le classpath (plus d’erreur « île introuvable » en dev). Mondes perso sans structures vanilla (preset SKYBLOCK).
- 1.3.0-alpha.6 — Plus de plateforme SKYBLOCK 3×3 : spawn via `SpawnResolver` API, l’île NBT suffit.
- 1.3.0-alpha.7 — Spawn île en 0 90 0 (NBT décalé d’autant).
