# Session — Displays GeckoLib calibrés du bâton

- **Date :** 2026-09-08
- **Version :** 1.3.0-alpha.31
- **Statut :** done

## Objectif

Corriger le placement en main/GUI avec GeckoLib (l’espace `builtin/entity` ≠ JSON classique).

## Fait

- Restauration `personnal_world_item_geckolib.json` + pack resource
- Clients Fabric/NeoForge : swap vers ce modèle puis wrap `isBuiltin()`
- JSON classique conservé pour le mode sans GeckoLib
