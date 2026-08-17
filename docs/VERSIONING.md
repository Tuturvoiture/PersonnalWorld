# Versionnement — PersonnalWorld

## Format

`{major}.{minor}.{patch}` en stable ; prereleases Modrinth-friendly :

| Stade | Format `mod.version` | Exemple jar (Stonecutter) |
|-------|----------------------|---------------------------|
| Alpha | `{major}.{minor}.0-alpha.{N}` | `personnalworld-fabric-1.3.0-alpha.0+1.21.1.jar` |
| Beta | `{major}.{minor}.0-beta.{N}` | `…-1.3.0-beta.0+1.21.1.jar` |
| Stable | `{major}.{minor}.{patch}` | `…-1.2.3+1.21.1.jar` |

Le suffixe `+{minecraft}` est ajouté **automatiquement** par Gradle / Stonecutter (`version = "${mod.version}+$minecraft"`). Ne pas le mettre dans `mod.version`.

| Segment | Rôle |
|---------|------|
| `major` | Changement d’ère produit — **uniquement** sur demande explicite |
| `minor` | Nouvelle ligne de fonctionnalités |
| `patch` / `alpha.N` / `beta.N` | **Chaque** correction ou petit changement fonctionnel |

## Règles d’incrément (obligatoires)

| Cas | Action |
|-----|--------|
| Correction / petit changement fonctionnel | Patch : `1.2.2` → `1.2.3` ; ou `1.3.0-alpha.0` → `1.3.0-alpha.1` |
| Gros ajout / nouvelle feature | Nouvelle mineure en alpha : `1.2.3` → `1.3.0-alpha.0`, puis `.1`… |
| Passage en beta | `1.3.0-alpha.N` → `1.3.0-beta.0` |
| Stabilisation | `1.3.0-beta.N` → `1.3.0` |
| Majeure `2.0` | Uniquement sur demande explicite |

À **chaque** livraison qui change le comportement en jeu :

1. Mettre à jour `mod.version` dans [`gradle.properties`](../gradle.properties).
2. Vérifier que les métadonnées restent branchées sur Gradle (`${version}` dans `fabric.mod.json` / `neoforge.mods.toml`) — **ne pas** hardcoder la version ailleurs.
3. Ajouter 1–3 bullets dans [`docs/CHANGELOG_WIP.md`](CHANGELOG_WIP.md).
4. Ajouter / compléter une entrée dans [`historique/sessions/`](historique/sessions/) (voir règle session-history).

Ne pas sauter de patch « pour plus tard ».

## Sources de vérité

| Élément | Fichier |
|---------|---------|
| Version mod | [`gradle.properties`](../gradle.properties) → `mod.version` |
| Métadonnées Fabric | [`fabric/src/main/resources/fabric.mod.json`](../fabric/src/main/resources/fabric.mod.json) (`${version}`) |
| Métadonnées NeoForge | [`neoforge/src/main/resources/META-INF/neoforge.mods.toml`](../neoforge/src/main/resources/META-INF/neoforge.mods.toml) (`${version}`) |
| Journal vivant (non publié) | [`docs/CHANGELOG_WIP.md`](CHANGELOG_WIP.md) |
| Releases archivées | [`docs/CHANGELOG.md`](CHANGELOG.md) |
| Capacités jouables | [`docs/CAPABILITIES.md`](CAPABILITIES.md) |
| Mémoire agent (prompts) | [`docs/historique/sessions/`](historique/sessions/) |
| Archive builds durable | [`builds/`](../builds/) (notes + META en Git ; jars locaux ignorés) |
| Build Gradle éphémère | `build/libs/<mod.version>/` (effacé par `clean`) |

## Flux release (« c’est en ligne »)

Quand une version est publiée (Modrinth / CurseForge) et que tu dis *« c’est en ligne »* ou *« release X.Y.Z publiée »* :

1. Déplacer les bullets `## Pending` de `CHANGELOG_WIP.md` vers une section `## [X.Y.Z] — YYYY-MM-DD` en tête de `CHANGELOG.md`.
2. Mettre à jour `Dernière version en ligne : X.Y.Z` dans `CHANGELOG_WIP.md` et vider `## Pending`.
3. Vérifier / mettre à jour [`CAPABILITIES.md`](CAPABILITIES.md) si des capacités ont changé.
4. Considérer `builds/X.Y.Z/` comme figé (ne plus l’écraser sauf rebuild explicite de cette version).
5. Proposer un tag Git `vX.Y.Z` (sans push sauf demande).

Pas de tâche Gradle qui tague seule.

## Archive `builds/`

Dossier **durable** à la racine, séparé de `build/` Gradle :

```
builds/
  INDEX.md
  <mod.version>/
    PATCHNOTES.md    # snapshot du WIP au build
    META.md          # date, rev Git, loaders
    jars/            # copies locales (gitignore)
```

Produit automatiquement par la tâche Gradle `writePendingPatchNotes` (branchée sur `build` et `buildAndCollect`).

## Hors scope

- Pas de `BUILD` séparé (le `+MC` Stonecutter suffit pour distinguer les artefacts multi-version).
- Pas de hash d’intégrité applicatif (contrairement à Ondule Connect).
- Les versions Minecraft cibles (`versions/1.21.1/…`) ne remplacent **pas** `mod.version`.
- Pas d’upload automatique Modrinth / CurseForge.
