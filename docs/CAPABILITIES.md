# Capacités par version

Ce qui est **jouable** / disponible pour une version donnée.  
Mettre à jour seulement quand une capacité apparaît, disparaît ou change (pas à chaque patch cosmétique).

## Courante — `1.4.0-beta.1`

| Capacité | Détail |
|----------|--------|
| Item monde perso | `personnal_world_item` — dim VOID DArchitect, île NBT |
| Bloc spawn | `spawn_marker` Y=88 ; pieds Y=89 |
| Animations bâton | GeckoLib optionnel |
| `/returnworld` | Retour depuis monde perso |
| Invitations / droits | `/pw` via façade `IslandMembersApi` ; cibles **online/offline** (`PlayerRef`) |
| Sync DA | DArchitect **≥ 0.1.2** ; TEMP préservés dans `applyRecord` |
| UI ready | `IslandMembersApi` + S2C `SyncIslandMembersPayload` + `IslandMembersClientCache` (pas encore d’écran livre / C2S) |
| Whitelist fichiers | `<world>/personnalworld/access/*.json` |
| Debug ops | `/pw debug …` si `enableDebugCommands` |
| BANNED | Enum prêt ; **pas** branché |
| Inventaire | Partagé par défaut (`shareInventory`) |
| Loader | Fabric 1.21.1 (NeoForge reporté) |

## Historique

| Version | Notes |
|----------|--------|
| 1.4.0-beta.1 | PATCHNOTES auto libs (agrégat beta/stable) |
| 1.4.0-beta.0 | Première beta 1.4 (invites / droits) |
| 1.4.0-alpha.5 | Façade UI + S2C sync membres |
| 1.4.0-alpha.4 | Fix wipe TEMP ; PlayerRef.resolve |
| 1.4.0-alpha.3 | Kick/visit/invite/role offline |
| 1.4.0-alpha.2 | (erreur doc hors-scope offline — annulé par alpha.3) |
| 1.4.0-alpha.1 | DA 0.1.2 ; sync kick/TEMP ; migration MANAGED |
| 1.4.0-alpha.0 | Invitations / droits îles + fichiers access |
| 1.3.1 | Sortie publique Fabric |
| 1.3.0 | Stabilisation Fabric 1.3 |
| 1.3.0-alpha.0 | Surcouche DArchitect |
| 1.2.2 | DimLib |
