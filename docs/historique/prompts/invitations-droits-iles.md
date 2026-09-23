# Invitations & droits îles

## P011

**Demande :** Patch note EN CurseForge pour sortie `1.4.0-beta.0`.

**Livré :** `builds/1.4.0-beta.0/CURSEFORGE_EN.md` (texte prêt à coller) ; lien depuis `RELEASE_NOTES_EN.md`.

**Pointeurs :** `builds/1.4.0-beta.0/CURSEFORGE_EN.md`

**Suite :** Publier sur CurseForge ; « c’est en ligne » si cut release.

## P010

**Demande :** PATCHNOTES dans chaque `build/libs/<ver>/` ; beta/official = agrégat sans doublons thématiques.

**Livré :** `VersionArchive` Gradle + `script/generate-patchnotes.ps1 -All` ; notes générées pour toutes les libs ; `1.4.0-beta.1`.

**Pointeurs :** `buildSrc/.../version-archive.kt`, `script/generate-patchnotes.ps1`, `docs/VERSIONING.md`, `builds/*/PATCHNOTES.md`

**Suite :** Commit/push si demandé.

## P009

**Demande :** Passer `1.4.0-alpha.9` (ligne 1.4) en **beta**.

**Livré :** `mod.version` → `1.4.0-beta.0` ; WIP / CAPABILITIES / RELEASE_NOTES_EN / session ; jar build OK.

**Pointeurs :** `gradle.properties`, `docs/RELEASE_NOTES_EN.md`, `docs/historique/sessions/2026-09-20_1.4.0-beta.0.md`

**Suite :** Commit/push si demandé ; « c’est en ligne » = cut release.
