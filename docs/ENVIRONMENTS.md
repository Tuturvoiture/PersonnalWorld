# Environnements

| Env | Rôle | Ce qui diffère |
|-----|------|----------------|
| **local** | `runClient` Fabric Loom | GeckoLib sur le classpath (anims visibles). Jars API dans `libs/`. `JAVA_HOME` = JDK 21. |
| **CI** | `.github/workflows/build.yml` | Build / collecte ; **pas** de publish. |
| **joueur** | dossier `mods/` | `personnalworld-fabric-*.jar` + Fabric API + Architectury + DArchitect ≥ 0.0.59. GeckoLib **optionnel**. |

**Publication actuelle :** Fabric uniquement. NeoForge = plus tard.

Aucune URL distante, aucun flag cloud. Release joueur = cut « c'est en ligne », pas un déploiement auto.
