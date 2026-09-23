# État courant

- **mod.version :** `1.4.0-alpha.18` (`gradle.properties`)
- **MC :** 1.21.1 — publication **Fabric seule** (NeoForge reporté)
- **Branche active :** `feature/livre-interface-iles` → `origin/feature/livre-interface-iles`
- **Jar :** non buildé (alpha en cours)
- **API :** DimensionArchitect `darchitect` ≥ **0.1.2** (`access()` public)
- **GeckoLib :** optionnel 4.7.5.1

## En cours — gros GUI (carnet + invites)

- Carnet `adventure_book` : modèle 3D + anims GeckoLib ; UI parchemin (onglets)
- Invitations / droits : `/pw`, `invite/*`, `IslandMembersApi`, sync S2C (fusionnés depuis `cursor/island-invites-permissions-73e0`)
- **À faire** : brancher l’UI carnet sur `IslandMembersApi` (liste îles réelle, permissions, presets)

## Ouvert (ne pas redécouvrir)

- Smoke multi-joueurs : kick → plus de build ; TEMP leave/restart ; visit hôte offline
- Unload/load public DA (0.1.3) pour `reload-island` complet
- Comportement **BANNED** (enum prêt) — plan futur
- Multi-îles réelles / switch active bâton
- Upload Modrinth/CurseForge 1.3.1 / cut tag — hors priorité GUI
- NeoForge / roadmap : pas maintenant
