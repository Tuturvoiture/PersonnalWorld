# Animations GeckoLib optionnelles du bâton

- **Date :** 2026-08-20
- **Version début / fin :** 1.3.0-alpha.8 → 1.3.0-alpha.9

## Prompt / objectif

Créer des animations GeckoLib pour le bâton (idle + clic-droit), sans exiger GeckoLib : s’il est absent, ne pas charger ses classes et ne pas crasher.

## Fait

- GeckoLib en `compileOnly` / `modLocalRuntime` (runClient) ; `suggests` Fabric et `optional` NeoForge.
- Item vanilla si GeckoLib absent ; `PersonnalWorldGeoItem` instancié uniquement via `Class.forName`.
- Animations `idle` (cube d’herbe qui tourne / flotte) et `use` (impulsion au téléport).
- Pack de ressources `geckolib_staff` (`builtin/entity`) enregistré seulement si l’item Geo a bien été créé.

## Fichiers touchés

- `gradle.properties`, `build.gradle.kts`, `fabric/build.gradle.kts`, `neoforge/build.gradle.kts`
- `fabric.mod.json`, `neoforge.mods.toml`
- `PersonnalWorld.java`, `PersonnalWorldContent.java`
- `compat/geckolib/GeckoLibHooks.java`, `PersonnalWorldGeoItem.java`
- `assets/.../geo`, `animations`, texture atlas, `resourcepacks/geckolib_staff`
- `PersonnalWolrdClient.java`, `PersonnalWorldNeoForgeClient.java`

## Décisions

- Pas d’import GeckoLib dans le chemin de chargement par défaut (factory + réflexion).
- Le JSON Blockbench reste le modèle par défaut ; overlay GeckoLib seulement si animations actives.

## Ouvert / à corriger

- Ajuster pivots / UV du `.geo.json` en jeu si le bâton est mal orienté.
- Vérifier NeoForge : pack `builtin/entity` + rendu Geo.

## WIP

- 1.3.0-alpha.9 — animations GeckoLib optionnelles du bâton.
