# Commandes

Une seule façon de lancer. Java **21** via `JAVA_HOME` (pas le `java` du PATH). Gradle : `gradlew.bat` à la racine.

| Action | Commande |
|--------|----------|
| Client Fabric (défaut) | `run-client.bat` |
| Client Fabric + hors-ligne | `run-client.bat --offline` |
| Client NeoForge | `run-client.bat :neoforge:1.21.1:runClient` |
| Client Fabric (Gradle direct) | `gradlew.bat :fabric:1.21.1:runClient` |
| Build | `gradlew.bat build` |
| Collecte jars | `gradlew.bat buildAndCollect` |
| Notes + archive `builds/` | `gradlew.bat writePendingPatchNotes` |

Ne pas inventer d’autre entrée. Détail OneDrive / daemon : commentaires dans `run-client.bat`.
