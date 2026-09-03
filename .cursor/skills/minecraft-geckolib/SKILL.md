---
name: minecraft-geckolib
description: >-
  Gère modèles, textures et animations Minecraft via GeckoLib / Blockbench pour
  PersonnalWorld (items GeoItem, soft-dep, chemins assets). À utiliser quand
  l’utilisateur parle d’animations, .geo.json, .animation.json, Blockbench,
  GeckoLib, bâton, idle/use, ou export bbmodel.
---

# Minecraft GeckoLib — PersonnalWorld

## Contexte projet

- Mod 1.21.1 Architectury (Fabric / NeoForge), soft-dep GeckoLib `4.7.5.1`.
- Item animé : `PersonnalWorldGeoItem` via `GeckoLibHooks` (`Class.forName`) — **ne jamais** importer GeckoLib hors `compat/geckolib/`.
- Sans GeckoLib : modèle JSON Blockbench classique (pas de crash).

## Chemins assets (source de vérité)

| Rôle | Chemin |
|------|--------|
| Geo | `src/main/resources/assets/personnalworld/geo/item/personnal_world_item.geo.json` |
| Anims | `src/main/resources/assets/personnalworld/animations/item/personnal_world_item.animation.json` |
| Texture geo | `src/main/resources/assets/personnalworld/textures/item/personnal_world_item.png` |
| Fallback JSON | `.../models/item/personnal_world_item.json` + `personnal_world_item_0/1.png` |
| Réf Blockbench | `docs/reference/blockbench/` |
| Java | `src/main/java/fr/galsaxx/compat/geckolib/PersonnalWorldGeoItem.java` |

## Anims actuelles (provisoires)

- Noms : `idle` (loop), `use` (trigger clic-droit).
- Bones : `staff`, `gem`.
- Si renommage Blockbench → aligner `RawAnimation` / `triggerableAnim` / `triggerAnim` dans `PersonnalWorldGeoItem`.

## Workflow Blockbench → mod

1. Ouvrir `docs/reference/blockbench/personnal_world_item_geckolib.bbmodel` (ou nouveau `geckolib_model`).
2. Éditer Animate ; exporter `.geo.json`, `.animation.json`, texture PNG.
3. Remplacer les fichiers assets ci-dessus (garder les noms de fichiers `personnal_world_item.*` sauf décision contraire + bump version).
4. Vérifier `texture_width` / `texture_height` du geo = taille PNG.
5. `run-client.bat` ou F3+T.

## Soft-dep (rappels)

- `fabric.mod.json` → `suggests.geckolib` ; NeoForge → `optional`.
- Renderer : modèle bake wrappé `isBuiltin()` (Fabric `ModelLoadingPlugin` / NeoForge `ModelEvent.ModifyBakingResult`).
- Ne pas exiger GeckoLib 4.9.x tant que Loom reste 1.10 (incompatible remap).

## Blockbench MCP (si connecté)

Plugin : [jasonjgardner/blockbench-mcp-plugin](https://github.com/jasonjgardner/blockbench-mcp-plugin) → `http://127.0.0.1:3000/bb-mcp` (Blockbench doit rester ouvert).

Si le serveur MCP `blockbench` est listé : piloter Blockbench live (geo, keyframes, screenshots). Sinon : éditer les JSON/PNG du repo. Setup : `docs/BLOCKBENCH_MCP_SETUP.md`.

## Versionnement

Toute livraison visuelle / anim fonctionnelle → bump `mod.version` + bullet `CHANGELOG_WIP` + entrée `docs/historique/sessions/`.
