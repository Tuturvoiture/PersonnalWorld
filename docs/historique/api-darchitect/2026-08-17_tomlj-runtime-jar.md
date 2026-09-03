# P0 — Jar `darchitect-*-*.jar` : Tomlj absent au runtime

- **Date :** 2026-08-17
- **Consommateur :** PersonnalWorld `1.3.0-alpha.1` (surcouche)
- **API visée :** DimensionArchitect **0.0.42** (`origin/main`)
- **Sévérité :** P0 — le client Fabric crash **avant le menu** si le joueur n’ajoute pas Tomlj à la main
- **Statut côté PW :** contourné en `modImplementation("org.tomlj:tomlj:1.1.1")` (dev + remap consommateur)

Copier cette fiche dans le Cursor du dépôt **DimensionArchitectAPI** et corriger **là-bas**, puis republier / recopier les jars dans `PersonnalWorld/libs/`.

## Repro (sans le contournement PW)

1. Dépendance consommateur : `modImplementation(files("libs/darchitect-fabric.jar"))` **sans** Tomlj Maven.
2. `runClient` Fabric 1.21.1.
3. Crash à l’entrypoint `darchitect` :

```
java.lang.RuntimeException: Could not execute entrypoint stage 'main' due to errors, provided by 'darchitect'
Caused by: java.lang.NoClassDefFoundError: org/tomlj/TomlInvalidTypeException
  at net.darchitect.fabric.DimensionArchitectFabric.onInitialize(...)
Caused by: java.lang.ClassNotFoundException: org.tomlj.TomlInvalidTypeException
```

Même échec attendu en **prod** : un joueur qui met uniquement `darchitect-fabric.jar` dans `mods/` (pas de module Maven transitif).

## Cause

`ConfigLoaderImpl` importe `org.tomlj.*` (dont `TomlInvalidTypeException`). Tomlj est déclaré en **`implementation`** seulement :

- `common/build.gradle.kts`
- `fabric/build.gradle.kts`
- `neoforge/build.gradle.kts`

Le `shadowJar` ne shadow que `shadowCommon` (classes common Architectury), **pas** Tomlj.  
`fabric.mod.json` ne déclare ni nested jar, ni `depends` Tomlj.  
`jar tf darchitect-fabric.jar` : **aucune** entrée `org/tomlj`.

Loom / `files(jar)` **ne tire pas** les transitives Maven du build DArchitect.

## Correctif attendu (API)

Rendre le **jar release autonome** pour le parseur TOML (Tomlj **et** sa transitive `org.antlr:antlr4-runtime`, sinon le crash suivant sera ANTLR).

Options (une suffit) :

1. **Fabric (recommandé)** — nested jar Loom :
   - `include(implementation("org.tomlj:tomlj:1.1.1"))` (transitives incluses) dans `fabric/build.gradle.kts`
   - vérifier `META-INF/jars/` dans le remap jar
2. **NeoForge** — JarJar / shadow Tomlj+ANTLR dans `remapJar` (pas seulement `shadowCommon`)
3. **Ne pas** se contenter de `implementation` : ça ne suffit ni au jar CurseForge, ni aux consommateurs `files(...)`.

### Vérifications

```text
jar tf build/libs/darchitect-fabric.jar | findstr /i "tomlj antlr META-INF/jars"
```

- runClient d’un consommateur **sans** `modImplementation("org.tomlj:…")` → menu principal, pas de NCDFE
- optionnel : test/check Gradle qui échoue si `org/tomlj` (ou nested jar) est absent du remap jar

Le module `examples/example-consumer` compile seulement `common` (`namedElements`) : il **ne détecte pas** ce trou. Un smoke « jar fichier tel que publié » serait utile.

## Contournement PersonnalWorld (à retirer après fix API)

- `fabric/build.gradle.kts` et `neoforge/build.gradle.kts` : `modImplementation("org.tomlj:tomlj:1.1.1")`
- Une fois le jar 0.0.x+ autonome recopié dans `libs/`, **retirer** cette ligne des deux loaders.

## Points annexes (même fiche, non bloquants)

### Docs install / Maven

`docs/fr/installation.md` cite `votre.group:dimension-architect-api` et `darchitect: ">=1.0.0"`.  
Les consommateurs réels (PW) utilisent des **jars locaux** `0.0.42`. Aligner la doc sur le mode de distribution réel (jars `dist/builds/` / `files(...)`) ou publier Maven.

### Javadoc `DimensionArchitect.builder(String)`

Le javadoc dit que l’id est **toujours** préfixé `darchitect:` (id local sans namespace).  
`DimensionId.parse` accepte `namespace:path` (ex. `personnalworld:perso_<uuid>`) après `registerMod`.  
Corriger le javadoc + `docs/fr/guide-demarrage.md` pour documenter les IDs namespacés des mods consommateurs.
