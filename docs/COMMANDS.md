# Commandes

Scripts dans [`script/`](../script/). Java **21** via `JAVA_HOME` (pas le `java` du PATH). Gradle wrapper : `.\gradlew.bat` à la racine (sous PowerShell : préfixe `.\` obligatoire).

| Action | Commande |
|--------|----------|
| Client Fabric **rapide** (défaut, incremental) | `script\run-client.bat` |
| Client Fabric **rebuild** (wipe `build/` + `--stop`) | `script\run-client-rebuild.bat` ou `script\run-client.bat --rebuild` |
| Client Fabric + hors-ligne | `script\run-client.bat --offline` |
| Client NeoForge | `script\run-client.bat :neoforge:1.21.1:runClient` |
| NeoForge + rebuild | `script\run-client.bat --rebuild :neoforge:1.21.1:runClient` |
| **Build tous les loaders** | `script\build-all.bat` |
| **Build Fabric seul** | `script\build-fabric.bat` |
| **Build NeoForge seul** | `script\build-neoforge.bat` |
| Notes + archive `builds/` | `.\gradlew.bat writePendingPatchNotes` |

Helper partagé : `script\_env.bat` (racine projet + JDK 21). Ne pas lancer seul.

Jars produits : `build\libs\<mod.version>\fabric\` et `...\neoforge\`.

Le mode **rebuild** sert surtout si OneDrive / daemon bloque `processResources` (« Failed to clean up stale outputs »). Au quotidien, préfère le mode rapide.
