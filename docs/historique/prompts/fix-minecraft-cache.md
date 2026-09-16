# fix-minecraft-cache

## P001

**Demande :** Script bash pour corriger un problème de cache des tests Minecraft qui bloque les sessions normales ; session de jeu buguée en cours à trouver.

**Livré :**
- `script/fix-minecraft-cache.sh` (+ helper `script/_fix-minecraft-cache-win.ps1`) : tue JVM runClient/Gradle, retire `session.lock` orphelins, options `--purge-loom` / `--kill-launcher-mc` / `--clean-build` / `--gradle-stop` / `--dry-run`
- Doc `docs/COMMANDS.md`
- Session trouvée : CurseForge **Arcadia: Echoes Of Power** (`javaw`) — laissée vivante (pas tuée sans `--kill-launcher-mc`) ; `session.lock` du `fabric/run` retiré

**Pointeurs :** `script/fix-minecraft-cache.sh`, `script/_fix-minecraft-cache-win.ps1`, `docs/COMMANDS.md`

**Suite :** Si Arcadia bloque encore → `./script/fix-minecraft-cache.sh --kill-launcher-mc` (ferme la partie). Si Loom corrompu → ajouter `--purge-loom`.

## P002

**Demande :** Notif fin des shells (151910 error / 151911 success).

**Livré :** Confirmé — 151910 = run interrompu (mauvais `gradlew` + download) ; 151911 = `fix-minecraft-cache.sh` OK (Arcadia laissée, locks projet nettoyés).

**Pointeurs :** `script/fix-minecraft-cache.sh`

**Suite :** inchangée (P001).

## P003

**Demande :** Les scripts fix doivent être en `.bat`, pas autre chose.

**Livré :** `script/fix-minecraft-cache.bat` (options identiques) ; suppression de `.sh` + `_fix-minecraft-cache-win.ps1` ; `docs/COMMANDS.md` / BDD mis à jour.

**Pointeurs :** `script/fix-minecraft-cache.bat`

**Suite :** Utiliser uniquement `script\fix-minecraft-cache.bat`.
