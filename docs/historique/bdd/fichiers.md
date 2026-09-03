# Carte fichiers

Regarder ici **avant** un grep projet.

## Build / version

- Version : `gradle.properties` → `mod.version`
- Fabric / NeoForge : `fabric/build.gradle.kts`, `neoforge/build.gradle.kts` ; métadonnées `${version}`
- Jars API : `libs/darchitect-fabric.jar`, `libs/darchitect-neoforge.jar` (≥ 0.0.59)
- PATCHNOTES API : `docs/historique/api-darchitect/PATCHNOTES-0.0.59.txt`
- Inventaire mondes perso : `PersonnalWorldUtil` → `DimensionBuilder.isolatePlayerData(false)`
- Bloc spawn : `PersonnalWorldContent.SPAWN_MARKER` ; `PersonalWorldSpawnReference` ; assets `textures/block/spawn_marker.*`

## Gameplay (`src/`)

- Entrée / item / bloc : `PersonnalWorld.java`, `PersonnalWorldContent.java`, `block/PersonalSpawnMarkerBlock.java`, `PersonnalWorldItem.java`
- Île / TP / commande : `util/IslandGenerator.java`, `util/PersonalWorldSpawnReference.java`, `util/PersonalWorldSpawnSafety.java`, `StructureCopier.java`, `PersonnalWorldUtil.java`, `ReturnPositionSaver.java`, `command/ReturnWorldCommand.java`
- Mixin : `mixin/PlayerEntityMixin.java`, `personnalworld.mixins.json` ; NBT `data/personnalworld/structure/ile_1.nbt`
- Loaders : `fabric/…/PersonnalWorldFabric.java`, `PersonnalWolrdClient.java` ; `neoforge/…/PersonnalWorldNeoForge.java`

## Bâton / GeckoLib

- Skill `.cursor/skills/minecraft-geckolib/` ; compat `src/main/java/fr/galsaxx/compat/`
- Geo / anims / modèles item + pack `resourcepacks/geckolib_staff/` ; MCP `docs/BLOCKBENCH_MCP_SETUP.md`

## Docs process

- Kit : `docs/kit-nouveau-projet.md` ; pont `AGENTS.md`
- Version / WIP / capacités : `docs/VERSIONING.md`, `CHANGELOG_WIP.md`, `CHANGELOG.md`, `CAPABILITIES.md`
- DoD / hors-scope / commandes / env : `docs/DEFINITION_OF_DONE.md`, `HORS_SCOPE.md`, `COMMANDS.md`, `ENVIRONMENTS.md`
- Migration DimLib (manuelle) : `docs/MIGRATION_DIMLIB.md`
- Sessions / API : `docs/historique/sessions/INDEX.md`, `docs/historique/api-darchitect/INDEX.md`
- Rules : `.cursor/rules/` — always = git-attribution, versioning, prompt-bdd
