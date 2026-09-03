# Environnements

| Env | Rôle | Ce qui diffère |
|-----|------|----------------|
| **local** | `runClient` Loom | GeckoLib sur le classpath (anims visibles). Jars API dans `libs/`. `JAVA_HOME` = JDK 21. |
| **CI** | `.github/workflows/build.yml` | Build / collecte ; **pas** de publish. |
| **joueur** | dossier `mods/` | `personnalworld-{fabric\|neoforge}-*.jar` + DArchitect ≥ 0.0.59 + Architectury (+ Fabric API). GeckoLib **optionnel**. |

Aucune URL distante, aucun flag cloud. Release joueur = cut « c'est en ligne », pas un déploiement auto.
