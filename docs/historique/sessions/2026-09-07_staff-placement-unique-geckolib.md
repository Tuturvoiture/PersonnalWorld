# Session — Unifier placement bâton + anim GeckoLib

- **Date :** 2026-09-07
- **Version :** 1.3.0-alpha.30
- **Statut :** done

## Objectif

Même hauteur / placements avec ou sans GeckoLib ; garder l’animation geo.

## Fait

- Suppression du second modèle `personnal_world_item_geckolib.json`
- Clients Fabric / NeoForge : wrap `isBuiltin()` uniquement sur le JSON classique
- Pack `geckolib_staff` : displays alignés sur le sans-GeckoLib
