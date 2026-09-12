# Carte fichiers

Regarder ici **avant** un grep projet.

## Build / version

- Version : `gradle.properties` → `mod.version`
- Fabric / NeoForge : `fabric/build.gradle.kts`, `neoforge/build.gradle.kts` ; métadonnées `${version}`
- Jars API : `libs/darchitect-fabric.jar`, `libs/darchitect-neoforge.jar` (≥ 0.0.59)
- PATCHNOTES API : `docs/historique/api-darchitect/PATCHNOTES-0.0.59.txt`
- Inventaire mondes perso : `PersonnalWorldUtil` → `isolatePlayerData(!shareInventory)` via `PersonnalWorldConfig`
- Config serveur : `config/PersonnalWorldConfig.java` → `config/personnalworld.toml`
- Bloc spawn : `PersonnalWorldContent.SPAWN_MARKER` ; `PersonalWorldSpawnReference` ; assets `textures/block/spawn_marker.*`

## Gameplay (`src/`)

- Entrée / item / bloc : `PersonnalWorld.java`, `PersonnalWorldContent.java`, `block/PersonalSpawnMarkerBlock.java`, `PersonnalWorldItem.java`
- Île / TP / commande : `util/IslandGenerator.java`, `util/PersonalWorldSpawnReference.java`, `util/PersonalWorldSpawnSafety.java`, `util/ReturnTeleport.java`, `PersonnalWorldUtil.java`, `ReturnPositionSaver.java`, `command/ReturnWorldCommand.java`
- Mixin : `mixin/PlayerEntityMixin.java`, `personnalworld.mixins.json` ; NBT `data/personnalworld/structure/ile_1.nbt`
- Loaders : `fabric/…/PersonnalWorldFabric.java`, `PersonnalWolrdClient.java` ; `neoforge/…/PersonnalWorldNeoForge.java`

## Bâton / GeckoLib

- Skill `.cursor/skills/minecraft-geckolib/` ; compat `src/main/java/fr/galsaxx/compat/`
- Placement sans GeckoLib : `models/item/personnal_world_item.json`
- Placement + anim GeckoLib : `models/item/personnal_world_item_geckolib.json` + geo/anims
- MCP `docs/BLOCKBENCH_MCP_SETUP.md`

- Kit : `docs/kit-nouveau-projet.md` ; pont `AGENTS.md`
- Version / WIP / capacités : `docs/VERSIONING.md`, `CHANGELOG_WIP.md`, `CHANGELOG.md`, `RELEASE_NOTES_EN.md`, `CAPABILITIES.md`
- DoD / hors-scope / commandes / env : `docs/DEFINITION_OF_DONE.md`, `HORS_SCOPE.md`, `COMMANDS.md`, `ENVIRONMENTS.md`
- Scripts : `script/` (`build-all.bat`, `build-fabric.bat`, `build-neoforge.bat`, `run-client.bat`, `run-client-rebuild.bat`, `_env.bat`)
- Migration DimLib (manuelle) : `docs/MIGRATION_DIMLIB.md`
- Sessions / API : `docs/historique/sessions/INDEX.md`, `docs/historique/api-darchitect/INDEX.md`
- Rules : `.cursor/rules/` — always = git-attribution, versioning, prompt-bdd
