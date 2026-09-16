# Mise en place versionnement long terme

- **Date :** 2026-08-16
- **Version début / fin :** 1.2.2 → 1.2.2 (tooling / docs, pas de bump jeu)

## Prompt / objectif

Mettre en place un versionnement lisible sur le long terme : changelog WIP → archive de release, patch notes au build, dossier `builds/` durable par version, et historique structuré des prompts pour pouvoir reprendre / corriger.

## Fait

- Docs : `CHANGELOG.md`, `CHANGELOG_WIP.md` (dernière en ligne), `CAPABILITIES.md`, `VERSIONING.md` enrichi
- Mémoire agent : `docs/historique/sessions/` + règles Cursor (`session-history`, `release-cut`, maj `changelog-wip`)
- Archive durable `builds/` + gitignore `builds/**/jars/`
- Tâche Gradle `writePendingPatchNotes` → `build/libs/<ver>/` et `builds/<ver>/` ; branchée sur `build` / `buildAndCollect` / chiseled ; CI mise à jour
- Vérifié : `./gradlew writePendingPatchNotes` (Java 21) a produit `builds/1.2.2/` + INDEX

## Fichiers touchés

- `docs/VERSIONING.md`, `docs/CHANGELOG.md`, `docs/CHANGELOG_WIP.md`, `docs/CAPABILITIES.md`
- `docs/historique/README.md`, `docs/historique/sessions/`
- `builds/INDEX.md`, `.gitignore`
- `.cursor/rules/session-history.mdc`, `release-cut.mdc`, `changelog-wip.mdc`
- `buildSrc/.../version-archive.kt`, `stonecutter.gradle.kts`, `.github/workflows/build.yml`

## Décisions

- Notes + META versionnés dans Git ; jars seulement en local (`builds/**/jars/`)
- Cut release manuel via phrase « c’est en ligne » (pas de tag Gradle automatique)
- Résumés de sessions structurés, pas de dump de transcripts Cursor

## Ouvert / à corriger

- Remplir `CAPABILITIES.md` / historique des releases passées si des notes joueur existent hors repo
- Au prochain publish : dire « c’est en ligne » pour poser la baseline « Dernière version en ligne »
- Parité NeoForge pour `/returnworld` (déjà noté dans CAPABILITIES)

## WIP

- Aucun bullet joueur (changement tooling uniquement)
