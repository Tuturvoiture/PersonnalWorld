# Commandes

Scripts dans [`script/`](../script/). Java **21** via `JAVA_HOME` (pas le `java` du PATH). Gradle wrapper : `.\gradlew.bat` à la racine (sous PowerShell : préfixe `.\` obligatoire).

| Action | Commande |
|--------|----------|
| Client Fabric **rapide** (défaut, incremental) | `script\run-client.bat` |
| Client Fabric **rebuild** (wipe `build/` + `--stop`) | `script\run-client-rebuild.bat` ou `script\run-client.bat --rebuild` |
| Client Fabric + hors-ligne | `script\run-client.bat --offline` |
| **Build Fabric** (publication actuelle) | `script\build-fabric.bat` |
| Notes + archive `builds/` | `.\gradlew.bat writePendingPatchNotes` |

Helper partagé : `script\_env.bat` (racine projet + JDK 21). Ne pas lancer seul.

Jar produit (cible release) : `build\libs\<mod.version>\fabric\`.

> NeoForge / `build-all` / `build-neoforge` : **reportés** — publication 1.3.x = Fabric seulement.

## Commandes in-game

`<joueur>` = joueur **connecté** (offline = hors scope, voir [`HORS_SCOPE.md`](HORS_SCOPE.md)).

| Commande | Qui | Effet |
|----------|-----|--------|
| `/returnworld` | Joueur dans une île perso | Retour position sauvée / spawn |
| `/pw invite <joueur> <co_creator\|builder\|visitor>` | Owner ou co-créateur | Whitelist persistante (JSON live + sync DA) |
| `/pw kick <joueur>` | Owner ou co-créateur | Retire whitelist/TEMP ; `clearRole`/`setRolesForDimension` ; expulse si présent |
| `/pw role <joueur> <role>` | Owner ou co-créateur | Change le rôle whitelist |
| `/pw list` | Joueur | Liste membres (+ TEMP) |
| `/pw visit <joueur> [nomIle]` | Joueur | Visite île active (hôte **connecté**) ; TEMP si pas membre |
| `/pw leave` | Visiteur sur une île | Quitte + retire TEMP + clearRole DA |
| `/pw debug setowner <cible> <nouveau>` | Op 4 + `enableDebugCommands` | Transfert owner logique |
| `/pw debug reload-access [cible]` | Op 4 + debug | Relit JSON access + resync DA |
| `/pw debug reload-island <cible>` | Op 4 + debug | Expulse, purge TEMP, resync ; unload DA encore best-effort (API publique unload = DA ≥ 0.1.3) |

### Accès / fichiers

- Whitelist : `<worldSave>/personnalworld/access/` (corrupt → `access/corrupt/`).
- Source de vérité = JSON PW ; DArchitect ≥ **0.1.2** = enforcer (`access()`).
- Config : `allowPassiveIslandVisit`, `enableDebugCommands` dans `config/personnalworld.toml`.
- **BANNED** : enum/schéma prêts, **pas** de `/pw ban`.
- **Offline** (kick/visit/invite/role) : **hors scope** jusqu’à demande explicite.

Doc technique sync DA : [`historique/api-darchitect/2026-09-17_access-api-public-sync-roles.md`](historique/api-darchitect/2026-09-17_access-api-public-sync-roles.md).
