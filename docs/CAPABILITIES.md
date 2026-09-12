# Capacités par version

Ce qui est **jouable** / disponible pour une version donnée.  
Mettre à jour seulement quand une capacité apparaît, disparaît ou change (pas à chaque patch cosmétique).

## Courante — `1.3.1`

| Capacité | Détail |
|----------|--------|
| Item monde perso | `personnal_world_item` — demande une dim VOID à DArchitect, pose l’île NBT |
| Bloc spawn | `spawn_marker` à **Y=88** (incassable, placement code-only) — référence TP ; pieds à Y=89 |
| Animations bâton | GeckoLib **optionnel** : idle + impulsion au clic-droit ; sans GeckoLib, modèle 3D statique |
| Commande `/returnworld` | Retour (uniquement depuis le monde perso) ; sinon message d’erreur |
| Création de dimension | **DimensionArchitect** `darchitect` ≥ 0.0.59 (`personnalworld:perso_<uuid>`) |
| Inventaire | **Partagé par défaut** (`shareInventory` dans `personnalworld.toml` ; ne rétroagit pas) |
| Config serveur | `config/personnalworld.toml` : denylists dims, cooldown bâton, inventaire |
| TP monde perso | Priorité marqueur Y+1 ; sinon scan 50 blocs ; bedrock Y=87 en dernier recours |
| Loader publié | **Fabric** 1.21.1 uniquement (NeoForge reporté) |
| Artefacts | `personnalworld-fabric-1.3.1+1.21.1.jar` |
| Migration DimLib | **Non automatique** — procédure manuelle (monde neuf + copie) : [`MIGRATION_DIMLIB.md`](MIGRATION_DIMLIB.md) |

## Historique

| Version | Notes |
|----------|--------|
| 1.3.1 | Sortie publique Fabric : config TOML, docs, nettoyage jar, depends resserrées |
| 1.3.0 | Première stabilisation Fabric 1.3 (DArchitect) — non publiée en storefront |
| 1.3.0-beta.0 | Première beta 1.3.0 (contenu alpha.32) |
| 1.3.0-alpha.20 | `spawn_marker` Y=88 ; texture BB à jour |
| 1.3.0-alpha.19 | Bloc `spawn_marker` référence spawn par dimension |
| 1.3.0-alpha.17 | Sécurité TP : sol sous spawn ou bedrock Y=87 |
| 1.3.0-alpha.16 | DArchitect 0.0.59 ; inventaire partagé explicite à la création de dim |
| 1.3.0-alpha.15 | DArchitect ≥ 0.0.57 (jars `libs/`) |
| 1.3.0-alpha.12 | Bâton : geo + texture depuis export Blockbench GeckoLib |
| 1.3.0-alpha.11 | Geo/textures du bâton réalignés sur Blockbench |
| 1.3.0-alpha.10 | Rendu GeckoLib du bâton réellement branché |
| 1.3.0-alpha.9 | Animations GeckoLib optionnelles du bâton |
| 1.3.0-alpha.8 | Item, assets, `/returnworld` dans le module commun (parité NeoForge) |
| 1.3.0-alpha.0 | Surcouche DArchitect ; DimLib retiré ; mondes DimLib non migrés |
| 1.2.2 | Item + `/returnworld` Fabric ; création via DimLib |
