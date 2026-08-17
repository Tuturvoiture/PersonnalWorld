# Capacités par version

Ce qui est **jouable** / disponible pour une version donnée.  
Mettre à jour seulement quand une capacité apparaît, disparaît ou change (pas à chaque patch cosmétique).

## Courante — `1.3.0-alpha.0`

| Capacité | Détail |
|----------|--------|
| Item monde perso | `personnal_world_item` — demande une dim VOID à DArchitect, pose l’île NBT |
| Commande `/returnworld` | Retour vers la position d’origine — **Fabric uniquement** |
| Création de dimension | **DimensionArchitect** `darchitect` ≥ 0.0.42 (`personnalworld:perso_<uuid>`) |
| NeoForge | Build + `registerMod` ; pas encore de parité item / commande |
| Minecraft | `1.21.1` (Stonecutter) |
| Artefacts | `personnalworld-{fabric\|neoforge}-1.3.0-alpha.0+1.21.1.jar` |

## Historique

| Version | Notes |
|---------|-------|
| 1.3.0-alpha.0 | Surcouche DArchitect ; DimLib retiré ; mondes DimLib non migrés |
| 1.2.2 | Item + `/returnworld` Fabric ; création via DimLib |
