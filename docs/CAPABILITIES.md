# Capacités par version

Ce qui est **jouable** / disponible pour une version donnée.  
Mettre à jour seulement quand une capacité apparaît, disparaît ou change (pas à chaque patch cosmétique).

## Courante — `1.4.0-alpha.2`

| Capacité | Détail |
|----------|--------|
| Item monde perso | `personnal_world_item` — dim VOID DArchitect, île NBT |
| Bloc spawn | `spawn_marker` Y=88 ; pieds Y=89 |
| Animations bâton | GeckoLib optionnel |
| `/returnworld` | Retour depuis monde perso |
| Invitations / droits | `/pw invite\|kick\|role\|list\|visit\|leave` (joueur **connecté**) ; sync DA ≥ 0.1.2 |
| Sync DA | DArchitect **≥ 0.1.2** : `access()`, `setRolesForDimension`, `clearRole`, migration `MANAGED`+owner |
| Whitelist fichiers | `<world>/personnalworld/access/*.json` (live + revision + quarantine) |
| Debug ops | `/pw debug setowner\|reload-access\|reload-island` si `enableDebugCommands` |
| BANNED | Enum/schéma prêts ; **pas** de ban/unban |
| Inventaire | Partagé par défaut (`shareInventory`) |
| Loader | Fabric 1.21.1 (NeoForge reporté) |

## Historique

| Version | Notes |
|----------|--------|
| 1.4.0-alpha.2 | Hors scope : pas de cible offline pour `/pw` invite/kick/role/visit |
| 1.4.0-alpha.1 | DA 0.1.2 ; sync kick/TEMP ; migration MANAGED |
| 1.4.0-alpha.0 | Invitations / droits îles + fichiers access |
| 1.3.1 | Sortie publique Fabric |
| 1.3.0 | Stabilisation Fabric 1.3 |
| 1.3.0-alpha.0 | Surcouche DArchitect |
| 1.2.2 | DimLib |
