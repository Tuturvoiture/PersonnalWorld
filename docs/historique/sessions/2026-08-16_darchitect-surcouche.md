# Remplacement DimLib par DArchitect

- **Date :** 2026-08-16
- **Version début / fin :** 1.2.2 → 1.3.0-alpha.0

## Prompt / objectif

Brancher PersonnalWorld comme surcouche de DimensionArchitect (`origin/main` 0.0.42) : l’API crée la dimension VOID ; le mod garde item, île NBT, commandes et téléport. Retrait de DimLib.

## Fait

- Rebuild des jars DArchitect 0.0.42 depuis `origin/main` ; copie dans `libs/`
- Gradle Fabric/NeoForge + métadonnées `darchitect >=0.0.42` ; DimLib retiré
- `registerMod("personnalworld", 64)` + `builder("personnalworld:perso_<uuid>").type(VOID).build()`
- Conservé : IslandGenerator, StructureCopier, mixin retour, `/returnworld`
- Supprimé : DimensionOptions / preset `the_void` / poll DimLib

## Fichiers touchés

- `libs/darchitect-fabric.jar`, `libs/darchitect-neoforge.jar`
- `fabric/build.gradle.kts`, `neoforge/build.gradle.kts`, `build.gradle.kts`
- `fabric.mod.json`, `neoforge.mods.toml`, `README.md`
- `PersonnalWorld.java`, `PersonnalWorldItem.java`, `PersonnalWorldUtil.java`
- `gradle.properties`, `docs/CHANGELOG_WIP.md`, `docs/CAPABILITIES.md`

## Décisions

- IDs inchangés : `personnalworld:perso_<uuid>`
- Quota DArchitect : 64
- Breaking alpha : pas de migration des mondes DimLib
- Loader Fabric aligné sur ≥ 0.18.4 (exigence DArchitect)

## Ouvert / à corriger

- Parité NeoForge item / `/returnworld`
- Tester en jeu la création VOID + pose île NBT

## WIP

- 1.3.0-alpha.0 — Les mondes perso sont créés via DimensionArchitect (dimension vide) : DimLib n’est plus requis.
- 1.3.0-alpha.0 — Breaking : les anciens mondes DimLib ne sont pas migrés.
