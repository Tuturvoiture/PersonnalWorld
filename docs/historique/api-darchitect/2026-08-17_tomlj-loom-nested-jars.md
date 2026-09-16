# P0 — Nested jars Tomlj ignorés par Loom `files()`

- **Date :** 2026-08-17
- **Consommateur :** PersonnalWorld `1.3.0-alpha.3`
- **API visée :** DimensionArchitect **0.0.43**
- **Sévérité :** P0 en **runClient Loom** (consommateur `files(jar)`) ; prod `mods/` probablement OK
- **Statut côté PW :** **corrigé** dans DArchitect 0.0.44 (shadow + relocate `net.darchitect.shadow.tomlj`) ; contournement Tomlj retiré dans PW `1.3.0-alpha.4`.

Copier dans le Cursor **DimensionArchitectAPI**. Suite de [2026-08-17_tomlj-runtime-jar.md](2026-08-17_tomlj-runtime-jar.md).

## Repro

1. Jar 0.0.43 : `META-INF/jars/tomlj-1.1.1.jar` + `antlr4-runtime` **et** `"jars"` dans `fabric.mod.json` (correct).
2. Consommateur : `modImplementation(files("libs/darchitect-fabric.jar"))` **sans** Tomlj Maven.
3. `runClient` Loom → crash identique à 0.0.42 :

```
NoClassDefFoundError: org/tomlj/TomlInvalidTypeException
  at net.darchitect.fabric.DimensionArchitectFabric.onInitialize(...)
ClassNotFoundException: org.tomlj.TomlInvalidTypeException
```

Crash PW : `fabric/run/crash-reports/crash-2026-08-17_22.02.55-client.txt` (mods : darchitect 0.0.43, personnalworld 1.3.0-alpha.2).

## Cause

Loom **remap** le jar `files(...)` pour le classpath de dev. Les nested jars Fabric (`include` / `META-INF/jars` + clé `jars`) sont vus par le loader **en prod** (`mods/`), mais **pas** après remap Loom du consommateur.

`include()` dans DArchitect 0.0.43 règle CurseForge / dossier `mods/`. Il ne règle pas un consommateur Architectury Loom qui dépend du jar publié via `files()`.

## Correctif attendu (API)

Une des options :

1. **Shadow** Tomlj + ANTLR **dans** les classes du remap jar (pas seulement nested), pour que Loom remap conserve `org.tomlj.*`.
2. Smoke Gradle : `example-consumer` en `modImplementation(files(publishedJar))` **sans** Tomlj Maven, puis `runClient` / GameTest — le `jar tf` seul est insuffisant.
3. Doc install : si un mod dépend via `files(darchitect-*.jar)`, déclarer aussi `org.tomlj:tomlj` (et ANTLR) jusqu’à (1).

## Contournement PersonnalWorld

`modImplementation("org.tomlj:tomlj:1.1.1")` Fabric + NeoForge. À retirer seulement quand un `runClient` consommateur **sans** cette ligne passe.

## Vérif 0.0.43 déjà OK (ne pas refaire)

- `jar tf` : `META-INF/jars/tomlj-1.1.1.jar`, `antlr4-runtime-4.11.1.jar`
- `fabric.mod.json` : `"jars": [ { "file": "META-INF/jars/..." }, ... ]`
