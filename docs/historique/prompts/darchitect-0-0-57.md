# DArchitect 0.0.57

## P001

**Demande :** MAJ DArchitect 0.0.57 depuis `dist/builds/0.0.57` ; incorporer le nouveau fichier ; reset versionning des sorties officielles.

**Livr� :** Clarif utilisateur � pas de reset (d�j� fait) ; historique intouchable. Plan : jars + PATCHNOTES + depends ?0.0.57 + alpha.15.

**Pointeurs :** `libs/darchitect-*.jar`, `docs/historique/api-darchitect/PATCHNOTES-0.0.57.txt`

**Suite :** Impl�menter le plan.

## P002

**Demande :** Ex�cuter le plan MAJ DimensionArchitect 0.0.57.

**Livr� :** Jars 0.0.57 dans `libs/` ; PATCHNOTES copi� ; depends ?0.0.57 ; `mod.version` 1.3.0-alpha.15 ; catch `DimensionAlreadyExistsException` ; compile Fabric/NeoForge OK ; docs courants + session + BDD.

**Pointeurs :** `gradle.properties`, `PersonnalWorldUtil.java`, `docs/historique/sessions/2026-08-30_darchitect-0.0.57.md`

**Suite :** Smoke en jeu si besoin ; commit sur demande.

## P003

**Demande :** Notifier le r�sultat de la compile Gradle en arri�re-plan.

**Livr� :** Confirm� � `:fabric:1.21.1:compileJava` + `:neoforge:1.21.1:compileJava` OK (exit 0).

**Pointeurs :** build Gradle local

**Suite :** �

## P004

**Demande :** Confirmer si tout est bon pour lancer le jeu.

**Livr� :** Compile OK ; smoke in-game non fait. Lancer via `run-client.bat` (ou mods/ avec DArchitect 0.0.57).

**Pointeurs :** `docs/COMMANDS.md`, `libs/darchitect-*.jar`

**Suite :** Feedback utilisateur apr�s lancement.

## P005

**Demande :** Inventaires non partag�s entre dims � DArchitect ou option PW oubli�e ?

**Livr� :** Comportement DArchitect : `persistence.isolate_player_data` **true par d�faut** (inventaire isol� `players/<uuid>.dat`). PersonnalWorld ne touche pas � cette conf.

**Pointeurs :** API `PersistenceConfig` ; conf jeu `config/darchitect-config.toml`

**Suite :** Si partage voulu ? `isolate_player_data = false` c�t� DArchitect (pas un oubli PW).

## P006

**Demande :** MAJ DArchitect 0.0.59 ; d�faut `isolate_player_data=false` ; PW garde inventaire partag� entre dims.

**Livr� :** Jars 0.0.59 ; `isolatePlayerData(false)` dans `PersonnalWorldUtil` ; alpha.16 ; docs + session + d�cision BDD.

**Pointeurs :** `PersonnalWorldUtil.java`, `docs/historique/sessions/2026-08-30_darchitect-0.0.59.md`

**Suite :** Recr�er mondes perso cr��s sous 0.0.57 si inventaire encore isol�.

## P007

**Demande :** S�curit� TP monde perso � scan 50 blocs sous spawn ; bedrock Y?3 si vide ; TP sur sol trouv� +1.

**Livr� :** `PersonalWorldSpawnSafety` ; branch� sur clic b�ton vers monde perso ; alpha.17.

**Pointeurs :** `PersonalWorldSpawnSafety.java`, `PersonnalWorldItem.java`

**Suite :** Test en jeu (�le absente / void).

## P008

**Demande :** Toujours TP � +1 du bloc de sol trouv�, pas seulement pour le bedrock.

**Livr� :** `teleportAboveGround(groundY)` commun ; scan depuis spawnY ; v�rif pieds libres ; alpha.18.

**Pointeurs :** `PersonalWorldSpawnSafety.java`

**Suite :** �

## P009

**Demande :** Impl�menter bloc `spawn_marker` (plan) � Y=90, bedrock secours conserv�.

**Livr� :** Bloc + assets + `PersonalWorldSpawnReference` + TP ; alpha.19.

**Pointeurs :** `PersonalWorldSpawnReference.java`, `PersonalSpawnMarkerBlock.java`

**Suite :** Test en jeu.

## P010

**Demande :** Corriger position `spawn_marker` ? Y=88 ; texture + JSON Blockbench � jour.

**Livr� :** `SPAWN_MARKER_Y=88` ; texture recopi�e ; alpha.20.

**Pointeurs :** `PersonnalWorld.java`, `PersonalWorldSpawnReference.java`

**Suite :** �

## P011

**Demande :** Texture spawn_marker noire en jeu.

**Livr� :** PNG recadr� 64�1024 (strip MC valide) ; mod�le `cube_all` ; alpha.21.

**Pointeurs :** `textures/block/spawn_marker.png`, `models/block/spawn_marker.json`

**Suite :** F3+T en jeu.

## P012

**Demande :** Animation spawn_marker incorrecte ; MCP Blockbench ouvert pour inspection.

**Livr� :** Texture 128�1920 (15 frames, ~7 fps) extraite du `.bbmodel` ; mod�le `cube_all` (export BB UV 4�4 invalides) ; `frametime` 3 ; alpha.22.

**Pointeurs :** `textures/block/spawn_marker.png`, `.mcmeta`, `models/block/spawn_marker.json`, `PersonnalWorld_Block_Spawn.bbmodel`

**Suite :** F3+T ; v�rifier animation en jeu sur marqueur Y=88.

## P013

**Demande :** Recentrer la texture vers le haut-gauche en 16�16.

**Livr� :** Mod�le elements + `texture_size` [128,128], UV `[0,0,16,16]` par face ; alpha.23.

**Pointeurs :** `models/block/spawn_marker.json`

**Suite :** F3+T.

## P014

**Demande :** UV 16�16 toujours mal align�es (motif en bas-gauche en jeu).

**Livr� :** Analyse PNG + BB : `texture_size` 64 (grille BB, pas 128) ; UV up/down non invers�es ; alpha.24.

**Pointeurs :** `models/block/spawn_marker.json`

**Suite :** F3+T v�rification visuelle.

## P015

**Demande :** Comparatif in-game vs Blockbench � glow doit �tre centr� sur chaque face.

**Livr� :** Analyse : glow centr� dans quadrant 64�64 (centro�de 31.5) ; texture recadr�e 64�960 + `cube_all` ; alpha.25.

**Pointeurs :** `textures/block/spawn_marker.png`, `spawn_marker.128.png` (backup)

**Suite :** F3+T comparer au preview BB.

## P016

**Demande :** Seule la tuile 16�16 haut-gauche (pas la grille 2�2).

**Livr� :** `cube_all` remplac� par elements + UV BB `[0,0,16,16]` / `texture_size` 64 ; alpha.26.

**Pointeurs :** `models/block/spawn_marker.json`

**Suite :** F3+T.

## P017

**Demande :** UV invers�es = mauvais quadrant ; strictement haut-gauche 16�16.

**Livr� :** PNG d�coup� 16�240 (pixels 0�16 haut-gauche par frame) ; `cube_all` sans inversion ; alpha.27.

**Pointeurs :** `textures/block/spawn_marker.png`, `models/block/spawn_marker.json`

**Suite :** F3+T.

## P018

**Demande :** Passer de 16�16 � **32�32** haut-gauche pour tester le rendu.

**Livr� :** PNG 32�480 d�coup� depuis `spawn_marker.128.png` ; `cube_all` inchang� ; alpha.28.

**Pointeurs :** `textures/block/spawn_marker.png`

**Suite :** F3+T comparer 16 vs 32.

## P019

**Demande :** Fix confirm� ; nettoyage instrumentation debug.

**Livr� :** Rendu OK en 32�32 haut-gauche (alpha.28) ; suppression `debug-d3e0b7.log`.

**Pointeurs :** `textures/block/spawn_marker.png` (32�480), `spawn_marker.128.png` (backup source)

**Suite :** �

## P020

**Demande :** Docs migration DimLib ; m�thode = monde neuf + copie des builds.

**Livr� :** `docs/MIGRATION_DIMLIB.md` ; liens README / HORS_SCOPE / CAPABILITIES / BDD. Pas de code ni bump version.

**Pointeurs :** `docs/MIGRATION_DIMLIB.md`

**Suite :** �

## P021

**Demande :** Abandon migration DimLib ; logo pour le mod.

**Livr� :** � (en attente direction style).

**Pointeurs :** `assets/personnalworld/icon.jpg`

**Suite :** Choix style + g�n�ration.


## P023

**Demande :** Logo mod � perspective cavali�re, sans fond (IA externe nulle).

**Livr� :** G�n�ration logo via Cursor GenerateImage.

**Pointeurs :** logo g�n�r� (� valider / int�grer icon.jpg)

**Suite :** Validation user + �ventuelle int�gration assets.
## P025

**Demande :** Logo ile : petite vache de face, plan d'eau + cascade dans le vide, contour blanc lisse.

**Livre :** docs/reference/personnalworld-logo.png (PNG transparent).

**Pointeurs :** docs/reference/personnalworld-logo.png

**Suite :** Validation ; integration icon.jpg si OK.

## P026

**Demande :** Supprimer le quadrillage du logo et am�liorer le contour blanc de l��le.

**Livr� :** Logo r�g�n�r� puis nettoy� en vrai PNG transparent ; contour blanc continu reconstruit autour de la silhouette.

**Pointeurs :** `docs/reference/personnalworld-logo.png`

**Suite :** Validation ; int�gration comme ic�ne du mod si souhait�e.
## P027

**Demande :** Retirer le halo inferieur et tout contour blanc ; corriger les blocs fusionnes qui cassent la perspective cavaliere.

**Livre :** Ile regeneree sur une grille voxel coherente, sans halo ni contour blanc, puis fond converti en transparence reelle.

**Pointeurs :** `docs/reference/personnalworld-logo-sans-contour.png`, `docs/reference/personnalworld-logo.png`

**Suite :** Validation ; integration comme icone si souhaitee.

## P028

**Demande :** Restaurer placements baton sans GeckoLib depuis personnal_world_item_sansgeckolib.json (Blockbench lance, MCP HS).

**Livre :** `personnal_world_item.json` + `_3d.json` avec display d origine ; textures remappees ; alpha.29.

**Pointeurs :** `models/item/personnal_world_item.json`, `docs/reference/blockbench/personnal_world_item_sansgeckolib.json`

**Suite :** Rebuild jar Fabric + F3+T / test sans GeckoLib.

## P029

**Demande :** Dossier script/ ; deplacer scripts ; build-all + build-fabric + build-neoforge.

**Livre :** `script/_env.bat`, `run-client.bat` (deplace), `build-all.bat`, `build-fabric.bat`, `build-neoforge.bat` ; `docs/COMMANDS.md` a jour ; ancien `run-client.bat` racine supprime.

**Pointeurs :** `script/`, `docs/COMMANDS.md`

**Suite :** —

## P030

**Demande :** Hauteur du baton incorrecte lorsque GeckoLib est present.

**Livre :** Diagnostic : second JSON display gecko (`personnal_world_item_geckolib`) diverge du placement sans GeckoLib.

**Pointeurs :** `models/item/personnal_world_item.json`, clients Fabric/NeoForge

**Suite :** Unifier placement sans + animation gecko.

## P031

**Demande :** Un seul placement (sans GeckoLib) + animation GeckoLib ; supprimer le double JSON.

**Livre :** Suppresssion `personnal_world_item_geckolib.json` ; wrap `isBuiltin()` sur le JSON classique ; pack resource displays = sans ; alpha.30.

**Pointeurs :** `PersonnalWolrdClient.java`, `PersonnalWorldNeoForgeClient.java`, `BuiltinStaffModelWrapper.java`

**Suite :** Test en jeu avec / sans GeckoLib (mains + idle).

## P032

**Demande :** run-client semble tout rebuild (MC inclus) pour peu de changements — est-ce necessaire ?

**Livre :** Explication : `script/run-client.bat` fait `--stop` + `rmdir` des `build/` → rebuild force ; pas requis pour un petit delta.

**Pointeurs :** `script/run-client.bat` (`UnlockBuildDirs`), `gradlew :fabric:1.21.1:runClient`

**Suite :** Relancer sans nettoyage si OneDrive ne bloque pas ; sinon garder le bat.

## P033

**Demande :** Baton mal place avec GeckoLib (placement unique ne marche pas).

**Livre :** Retabli `personnal_world_item_geckolib.json` (displays calibrés builtin/entity) ; clients swap + wrap ; JSON classique pour sans GeckoLib ; alpha.31.

**Pointeurs :** `personnal_world_item_geckolib.json`, `PersonnalWolrdClient.java`, `PersonnalWorldNeoForgeClient.java`

**Suite :** F3+T / test 1P 3P GUI avec GeckoLib.

## P034

**Demande :** Erreur terminal (ref L509-L530) — `gradlew.bat` non reconnu en PowerShell.

**Livre :** Explication : sous PS il faut `.\gradlew.bat` (pas `gradlew.bat` nu).

**Pointeurs :** `.\gradlew.bat :fabric:1.21.1:runClient`, `script\run-client.bat`

**Suite :** —

## P035

**Demande :** run-client : un mode sans rebuild MC, un avec rebuild.

**Livre :** Defaut = incremental ; `--rebuild` / `run-client-rebuild.bat` = --stop + wipe build/ ; `docs/COMMANDS.md` a jour.

**Pointeurs :** `script/run-client.bat`, `script/run-client-rebuild.bat`, `docs/COMMANDS.md`

**Suite :** —

## P036

**Demande :** `.\script\run-client.bat` → erreur « . etait inattendu ».

**Livre :** Fix echo : parentheses dans un bloc `else (` cassaient le parsing cmd.

**Pointeurs :** `script/run-client.bat`

**Suite :** Relancer `.\script\run-client.bat`

## P037

**Demande :** Terminal L573-L647 — compile fail `ctx.id()` introuvable.

**Livre :** `PersonnalWolrdClient` utilise `resourceId()` / `topLevelId()` (Fabric model-loading 2.x) ; alpha.32.

**Pointeurs :** `fabric/.../PersonnalWolrdClient.java`

**Suite :** `.\script\run-client.bat` puis test bâton + GeckoLib.

## P038

**Demande :** Fichier MD anglais pour changelog sorties publiques / beta.

**Livre :** `docs/RELEASE_NOTES_EN.md` (notes storefront EN + template) ; pointeur dans `VERSIONING.md`.

**Pointeurs :** `docs/RELEASE_NOTES_EN.md`

**Suite :** Coller la section voulue sur Modrinth/CurseForge au cut beta/public.

## P039

**Demande :** RELEASE_NOTES_EN sans GitHub / NeoForge ; fichier pret a copier-coller.

**Livre :** `docs/RELEASE_NOTES_EN.md` reduit a un bloc storefront Fabric-only.

**Pointeurs :** `docs/RELEASE_NOTES_EN.md`

**Suite :** —

## P040

**Demande :** Jar fabric alpha.32 passe en beta.

**Livre :** `mod.version` = `1.3.0-beta.0` ; WIP ; `RELEASE_NOTES_EN.md` ; capacités / README / session.

**Pointeurs :** `gradle.properties`, `docs/RELEASE_NOTES_EN.md`

**Suite :** `script\build-fabric.bat` puis publier le jar `…-1.3.0-beta.0+1.21.1.jar` ; dire « c'est en ligne » pour cut changelog FR.

## P041

**Demande :** Beta en ligne pour sortie utilisable + retours joueurs.

**Livre :** `RELEASE_NOTES_EN.md` : bandeau beta / feedback ; WIP precise beta publique.

**Pointeurs :** `docs/RELEASE_NOTES_EN.md`

**Suite :** Build + publish ; puis « c'est en ligne » pour archiver le changelog FR.

## P042

**Demande :** commit + pull request (sans attribution assistant).

**Livre :** Commit `0bcdd37` auteur GalsaxX_FR (sans Co-authored Cursor) ; push ; PR #4 mise a jour.

**Pointeurs :** https://github.com/Tuturvoiture/PersonnalWorld/pull/4

**Suite :** —

## P043

**Demande :** Pourquoi pas tout committe ?

**Livre :** Commit journal prompts restant ; exclus body/headers/debug (dumps MCP/debug).

**Pointeurs :** docs/historique/prompts/

**Suite :** —
