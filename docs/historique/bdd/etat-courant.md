# État courant

- **mod.version :** `1.3.0-alpha.19` (`gradle.properties`)
- **MC :** 1.21.1 — Stonecutter + Architectury (common / Fabric / NeoForge)
- **Branche :** `feature/multi-version-multi-loader` — **gros working tree non commité**
- **Release :** aucune en ligne ; Pending dans `docs/CHANGELOG_WIP.md`
- **API :** DimensionArchitect `darchitect` ≥ 0.0.59 (jars `libs/darchitect-*.jar`)
- **Inventaire :** partagé Overworld ↔ monde perso (`isolatePlayerData(false)` à la création)
- **GeckoLib :** optionnel, cible 4.7.5.1 (pas 4.9.2)

## Ouvert (ne pas redécouvrir)

- Vérif en jeu bâton : placements 1P/3P/GUI + idle (F3+T)
- Smoke NeoForge : modèle, clic-droit, `/returnworld`, inventaire partagé
- Mondes perso créés sous DArchitect 0.0.57 (isolation par défaut) : recréer la dim ou `/darchitect delete` si inventaire encore isolé
- Commit du delta (sur demande)
- Roadmap invitations / menu / types d’îles : pas commencé
- Migration DimLib : doc seule (`docs/MIGRATION_DIMLIB.md`) — pas de code
