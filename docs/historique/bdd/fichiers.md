# Carte fichiers

Regarder ici **avant** un grep projet.

## Build / version

- Version : `gradle.properties` → `mod.version`
- Fabric / NeoForge : `fabric/build.gradle.kts`, `neoforge/build.gradle.kts` ; métadonnées `${version}`
- Jars API : `libs/darchitect-fabric.jar`, `libs/darchitect-neoforge.jar` (≥ **0.1.2**)
- PATCHNOTES API : `docs/historique/api-darchitect/PATCHNOTES-0.1.2.txt`
- Rapport invitations / accès public : `docs/historique/api-darchitect/2026-09-17_access-api-public-sync-roles.md` (statut intégré)
- PATCHNOTES par version : `script/generate-patchnotes.ps1 -All` ou Gradle `generateAllLibsPatchNotes` → `build/libs/<ver>/` + `builds/<ver>/`. Jar joueur à chaque `build` : `builds/<ver>/<ver>-<loader>.jar` (gitignore).
- Inventaire mondes perso : `PersonnalWorldUtil` → `isolatePlayerData(!shareInventory)` via `PersonnalWorldConfig`
- Config serveur : `config/PersonnalWorldConfig.java` → `config/personnalworld.toml` ; sync DA : `config/DArchitectQuotaSync.java`
- Bloc spawn : `PersonnalWorldContent.SPAWN_MARKER` ; `PersonalWorldSpawnReference` ; assets `textures/block/spawn_marker.*`

## Gameplay (`src/`)

- Entrée / item / bloc : `PersonnalWorld.java`, `PersonnalWorldContent.java`, `block/PersonalSpawnMarkerBlock.java`, `PersonnalWorldItem.java`
- Île / TP / commande : `util/IslandGenerator.java`, `util/PersonalWorldSpawnReference.java`, `util/PersonalWorldSpawnSafety.java`, `util/ReturnTeleport.java`, `PersonnalWorldUtil.java`, `ReturnPositionSaver.java`, `command/ReturnWorldCommand.java`, `command/PersonnalWorldCommand.java`
- Invitations / droits : `invite/` — façade UI `IslandMembersApi` → `IslandAccessService` / `AccessFileStore` / `DArchitectAccess` ; garde `PresenceAndRightsGuard` ; visiteur `island/IslandRoleGuard.java` ; fichiers `<world>/personnalworld/access/`
- Réseau UI : `network/SyncIslandMembersPayload` (membres) ; `network/IslandBookNetworking` (listes îles, presets, réglages) + `fabric/…/client/IslandBookClient`
- Offline / TEMP : `PlayerRef.resolve` ; visit → `PersonnalWorldUtil.openExistingPersonalWorld` (reload DA si dim déchargée) ; TEMP réinjectés dans `DArchitectAccess.applyRecord` ; `TempVisitorStore.clearAll` au start
- Vérif delta 1.3.1→1.4.0 : `docs/historique/sessions/2026-10-01_verif-depuis-1.3.1.md` ; cut : `builds/1.4.0/CURSEFORGE_EN.md`
- Mixin : `mixin/PlayerEntityMixin.java`, `personnalworld.mixins.json` ; NBT `data/personnalworld/structure/ile_1.nbt`
- Loaders : `fabric/…/PersonnalWorldFabric.java`, `PersonnalWolrdClient.java` ; `neoforge/…/PersonnalWorldNeoForge.java`

## Bâton / GeckoLib

- Skill `.cursor/skills/minecraft-geckolib/` ; compat `src/main/java/fr/galsaxx/compat/`
- Placement sans GeckoLib : `models/item/personnal_world_item.json`
- Placement + anim GeckoLib : `models/item/personnal_world_item_geckolib.json` + geo/anims
- MCP `docs/BLOCKBENCH_MCP_SETUP.md`

## Carnet d'aventurier (1.4.0-alpha.1)

- Item commun : `AdventureBookItem.java` (envoie Open payload)
- Item GeckoLib : `compat/geckolib/AdventureBookGeoItem.java` (animations + Open payload)
- Réseau : `network/OpenAdventureBookPayload.java`, `network/CloseAdventureBookPayload.java` (CustomPayload)
- GeckoLibHooks : `createBookItem()` via Class.forName
- Assets : `geo/item/adventure_book.geo.json`, `animations/item/adventure_book.animation.json`, `textures/item/adventure_book.png`
- Models : `models/item/adventure_book.json` (fallback), `models/item/adventure_book_geckolib.json` (builtin/entity)
- Référence Blockbench : `docs/reference/blockbench/adventure_book.bbmodel`
- Écran îles : `fabric/…/client/AdventureBookScreen.java` (Mes îles / Îles invitées, parchemin existant). Continuité : `docs/ISLAND_UI.md`. Règles pures : `src/main/java/fr/galsaxx/island/`
- Boutons custom : `fabric/…/client/AdventureBookImageButton.java` (presets / permissions)
- Texture GUI : `textures/gui/adventure_book_parchment.png` (256×192) ; `island_button.png` (logo île, boutons V)
- Pose 3P lecture : `client/AdventureBookClientPose.java` + `mixin/client/PlayerEntityRendererMixin.java`
- Sens open main : `compat/geckolib/client/AdventureBookGeoRenderer.java`

## Docs / process

- Version / WIP / capacités : `docs/VERSIONING.md`, `CHANGELOG_WIP.md`, `CHANGELOG.md`, `RELEASE_NOTES_EN.md`, `CAPABILITIES.md`
- DoD / hors-scope / commandes / env : `docs/DEFINITION_OF_DONE.md`, `HORS_SCOPE.md`, `COMMANDS.md`, `ENVIRONMENTS.md`
- Scripts : `script/` (`build-all.bat`, `build-fabric.bat`, `build-neoforge.bat`, `run-client.bat`, `run-client-rebuild.bat`, `fix-minecraft-cache.bat`, `_env.bat`)
- Migration DimLib (manuelle) : `docs/MIGRATION_DIMLIB.md`
- Sessions / API : `docs/historique/sessions/INDEX.md`, `docs/historique/api-darchitect/INDEX.md`
- Rules : `.cursor/rules/` — always = git-attribution, versioning, prompt-bdd
