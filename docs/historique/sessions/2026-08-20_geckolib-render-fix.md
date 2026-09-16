# Fix rendu animé GeckoLib 4.9.2

- **Date :** 2026-08-20
- **Version début / fin :** 1.3.0-alpha.9 → 1.3.0-alpha.10

## Prompt / objectif

GeckoLib 4.9.2 est chargé mais le bâton n’a aucune animation.

## Fait

- Cause : le JSON Blockbench restait le modèle rendu (`isBuiltin` false) ; l’override `getGeoItemRenderer` compilé contre 4.7 ne matchait plus l’API 4.9 (`GeoItemRenderer` au lieu du BEWLR).
- Wrapper de modèle `isBuiltin()` après bake (Fabric `ModelLoadingPlugin`, NeoForge `ModelEvent.ModifyBakingResult`).
- Compile / runClient sur GeckoLib **4.7.5.1** (4.9.2 ne se remap pas avec Loom 1.10 ; elle exige aussi Fabric API ≥ 0.116.12).

## Fichiers touchés

- `PersonnalWorldGeoItem.java`, `BuiltinStaffModelWrapper.java`
- `PersonnalWolrdClient.java`, `PersonnalWorldNeoForgeClient.java`
- `gradle.properties`, `versions/1.21.1/gradle.properties`
- modèles item (texture particle namespacée)

## Décisions

- Ne plus dépendre d’un resource pack overlay pour `builtin/entity`.

## Ouvert / à corriger

- Vérifier idle + clic-droit en jeu.

## WIP

- 1.3.0-alpha.10 — rendu GeckoLib du bâton réellement branché.
