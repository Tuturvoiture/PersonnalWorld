# Capacités par version

Ce qui est **jouable** / disponible pour une version donnée.  
Mettre à jour seulement quand une capacité apparaît, disparaît ou change (pas à chaque patch cosmétique).

## Courante — `1.4.0-beta.7`

| Capacité | Détail |
|----------|--------|
| Item monde perso | `personnal_world_item` — dim VOID DArchitect, île NBT ; suit l’île **active** |
| Multi-îles | Plusieurs îles / joueur ; plafond `maxIslandsPerPlayer` (défaut 3) ; ids `perso_<uuid>` / `perso_<uuid>_<n>` |
| Bloc spawn | `spawn_marker` Y=88 ; pieds Y=89 ; déplaçable via carnet (owner) |
| Carnet d’aventurier | Item + GUI parchemin (Mes îles / Îles invitées, presets, réglages, invitations) |
| Presets | `classic` déverrouillé (`ile_1`) ; `forest` / `rock` / `desert` verrouillés (« bientôt ») ; fallback structure `ile_1` |
| Animations | GeckoLib optionnel (bâton + carnet) ; close via état `closing` (pas de NBT en fin d’anim) |
| `/returnworld` | Retour depuis monde perso |
| Invitations / droits | `/pw` + carnet via `IslandMembersApi` ; cibles **online/offline** (`PlayerRef`) |
| Rôles | Owner, co-créateur, builder, visitor, TEMP ; co-créateur gère invites de l’île affichée |
| Visiteur | JOIN OK ; pas casse / pose / coffres / dégâts entités ; bâton chez hôte → retour sans sauver l’île |
| Sync DA | DArchitect **≥ 0.1.2** ; TEMP préservés ; `max_simultaneous` aligné sur 64 (`syncDarchitectMaxSimultaneous`) |
| Whitelist fichiers | `<world>/personnalworld/access/*.json` |
| Messages | TP en barre d’action (`actionBarMessages`) ; invite reçue en tchat |
| Debug ops | `/pw debug …` si `enableDebugCommands` |
| BANNED | Enum prêt ; **pas** branché |
| Inventaire | Partagé par défaut (`shareInventory`) |
| Loader | Fabric 1.21.1 (NeoForge reporté) |

## Historique

| Version | Notes |
|----------|--------|
| 1.4.0-beta.7 | Close carnet sans flash / hop (état `closing`) |
| 1.4.0-beta.5 | 4 presets sur une ligne |
| 1.4.0-beta.3 | Presets non prêts verrouillés ; fallback `ile_1` |
| 1.4.0-beta.2 | Beta carnet + invites + droits + sync DA max |
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
