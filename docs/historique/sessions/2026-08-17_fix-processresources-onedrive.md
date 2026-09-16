# Fix processResources OneDrive

- **Date :** 2026-08-17
- **Version début / fin :** 1.3.0-alpha.2 (inchangée — script / Gradle local)

## Prompt / objectif

`.\run-client.bat` échoue : `:fabric:1.21.1:processResources` → `Failed to clean up stale outputs`.

## Fait

- Cause probable : OneDrive verrouille `build/resources` (le script ne nettoyait que `build/classes`).
- `run-client.bat` : nettoyage de tout `*\versions\1.21.1\build`.
- `gradle.properties` : `org.gradle.vfs.watch=false`.

## Fichiers touchés

- `run-client.bat`
- `gradle.properties`

## Décisions

- Pas de bump : pas de changement en jeu.

## Ouvert / à corriger

- Relancer `.\run-client.bat` ; si le WARNING de suppression persiste, pause OneDrive sur le dossier du projet.

## WIP

- (aucun)
