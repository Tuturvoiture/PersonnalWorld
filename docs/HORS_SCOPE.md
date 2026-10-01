# Hors scope

Ce que PersonnalWorld **n’est pas**. L’agent n’élargit pas tout seul.

- Pas un remplaçant de DimensionArchitect : création / cycle de vie des dimensions = API sœur. Ne pas modifier ce dépôt depuis ce workspace.
- Pas de migrateur automatique DimLib → 1.3 (breaking) : procédure manuelle « monde neuf + copie » dans [`MIGRATION_DIMLIB.md`](MIGRATION_DIMLIB.md).
- Pas d’upload automatique Modrinth / CurseForge.
- Pas de majeure `2.0` sans demande explicite.
- Rôle **BANNED** : enum/schéma prêts, **comportement non branché** (pas de ban/unban) — à reprendre plus tard.
- Contrôle météo depuis le carnet : affiché « bientôt », pas branché.
- Types d’île forêt / roche / désert : visibles mais **verrouillés** jusqu’aux structures NBT ; pas de biomes custom hors presets.
- Refonte graphique du carnet (rendu / textures / mise en page) : lot à part ; l’écran actuel est fonctionnel seulement.
- Unload/load dim via API DA publique : attendre DArchitect ≥ 0.1.3 ; `reload-island` reste best-effort.
- **Pas de release NeoForge (ni autre loader) tant que la ligne Fabric 1.4 n’est pas stabilisée** : le module NeoForge peut rester dans le repo, mais hors priorité publish / smoke / storefront.
- Pas de télémétrie, pas de compte cloud, pas d’auth joueur.
