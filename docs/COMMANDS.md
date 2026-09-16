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
