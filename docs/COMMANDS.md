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

Le mode **rebuild** sert surtout si OneDrive / daemon bloque `processResources` (« Failed to clean up stale outputs »). Au quotidien, préfère le mode rapide.

## Commandes in-game

| Commande | Qui | Effet |
|----------|-----|--------|
| `/returnworld` | Joueur dans une île perso | Retour position sauvée / spawn |
| `/pw invite <joueur> <co_creator\|builder\|visitor>` | Owner ou co-créateur | Whitelist persistante (JSON live) |
| `/pw kick <joueur>` | Owner ou co-créateur | Retire whitelist/temp + expulse si présent |
| `/pw role <joueur> <role>` | Owner ou co-créateur | Change le rôle whitelist |
| `/pw list` | Joueur | Liste membres (+ TEMP) |
| `/pw visit <joueur> [nomIle]` | Joueur | Visite île active (ou nommée) en TEMP si pas déjà membre |
| `/pw leave` | Visiteur sur une île | Quitte + retire TEMP |
| `/pw debug setowner <cible> <nouveau>` | Op 4 + `enableDebugCommands` | Transfert owner logique |
| `/pw debug reload-access [cible]` | Op 4 + debug | Relit JSON access |
| `/pw debug reload-island <cible>` | Op 4 + debug | Expulse, purge TEMP, resync ; unload DA best-effort |

Fichiers whitelist : `<worldSave>/personnalworld/access/` (corrupt → `access/corrupt/`).  
**BANNED** : présent en code/schéma, **pas** de `/pw ban` pour l’instant.
