# livre-interface-iles

Conversation Cursor sur la branche `feature/livre-interface-iles`.

## P001 — Demande initiale : carnet 3D + animations
**Demande :** Créer item carnet d'aventurier 3D GeckoLib avec 4 animations (idle_closed, open, idle_open, close), ouverture en deux entre pages.  
**Livré :** Branche créée ; structure Blockbench MCP établie ; premier prototype géométrie.  
**Pointeurs :** `docs/reference/blockbench/adventure_book.bbmodel`  
**Suite :** Textures + animations.

## P002 — Ajout animations + Java
**Demande :** Animations + implémentation Java complète + GUI placeholder.  
**Livré :** Plan animations détaillé.  
**Suite :** Exécution.

## P003 — Note suivi
**Demande :** Notons les demandes pour comparer à la fin.  
**Livré :** Grid demandes créée (voir P014).  
**Suite :** —

## P004 — Branche
**Demande :** Passe sur nouvelle branche.  
**Livré :** `git checkout -b feature/livre-interface-iles`.  
**Suite :** —

## P005 — Reconnexion MCP
**Demande :** Si problème MCP, le signaler.  
**Livré :** MCP rétabli via `mcp_auth`.  
**Suite :** —

## P006 — Export + commit
**Demande :** Export bbmodel + assets ; oui créer branche + commit.  
**Livré :** Plan confirmé.  
**Suite :** —

## P007 — Exécution plan
**Demande :** Implement the plan as specified.  
**Livré :** Premier modèle (cubes + textures de base).  
**Suite :** —

## P008 — Refus textures « 5 ans »
**Demande :** Refaire entièrement, textures belles et relief.  
**Livré :** Redesign géométrie en cours.  
**Suite :** Validation forme avant textures.

## P009 — Exécution
**Demande :** Implement the plan as specified (double).  
**Livré :** Géométrie améliorée.  
**Suite :** —

## P010 — Dimensions décimales
**Demande :** Tu as le droit aux dimensions décimales.  
**Livré :** Géométrie décimale appliquée.  
**Suite :** —

## P011 — Textures plates dynamiques + 124×124 + split
**Demande :** Textures dynamiques (rubans, corde), 124×124, split milieu obligatoire.  
**Livré :** pages_back / pages_front split à Z=0.  
**Suite :** —

## P012 — Mieux mais pas fini
**Demande :** Mieux mais pas fini + pas de cut au milieu visible.  
**Livré :** Split séparé.  
**Suite :** —

## P013 — Image référence + style
**Demande :** [image référence livre violet chunky] — veux ce style adapté au mod.  
**Livré :** Redesign complet (coins proéminents, reliure crêtes, sangle, rubans).  
**Pointeurs :** Screenshot Blockbench.  
**Suite :** Validation géo.

## P014 — Tweaks + validation géo + rubans fins
**Demande :** Tweaks geom (modifs déjà faites) + rubans très fins jusqu'en haut + carnet simple sans île + symétrie.  
**Livré :**
- Rubans redessinés (0.4u wide, Y=17.5→-11)
- Texture carnet d'aventurier (cuir, filigrane compas)
- Symétrie back_c_br corrigée
- 4 animations GeckoLib créées et exportées
- Java complet : AdventureBookItem, AdventureBookGeoItem, GeckoLibHooks.createBookItem()
- AdventureBookScreen placeholder + réseau Architectury
- PersonnalWorldContent.ADVENTURE_BOOK, lang EN+FR
- Version `1.4.0-alpha.0`, CHANGELOG_WIP, commit `c8aefc7`  
**Pointeurs :**
- Géo : `src/main/resources/assets/personnalworld/geo/item/adventure_book.geo.json`
- Anim : `src/main/resources/assets/personnalworld/animations/item/adventure_book.animation.json`
- Java : `AdventureBookItem`, `AdventureBookGeoItem`, `GeckoLibHooks`  
**Suite :** Test en jeu ; phase 2 interface.

---

## P015 — Présent en jeu ?
**Demande :** Si je lance le jeu, l'item est-il présent ?  
**Livré :** Oui après rebuild ; créatif Outils ; GeckoLib pour 3D.  
**Suite :** —

## P016 — Comment récupérer l'item ?
**Demande :** Comment récupérer l'item ?  
**Livré :** Créatif Outils ou `/give @s personnalworld:adventure_book` ; pas de craft.  
**Suite :** —

## P017 — Erreurs compile terminal
**Demande :** Logs compile L518–632 (sendToPlayer + variable book).  
**Livré :** Migration CustomPayload Architectury 13 ; fix book ; bump 1.4.0-alpha.1. Cache Loom local encore verrouillé côté agent — relancer `run-client-rebuild.bat` après `gradlew --stop` + purge `.gradle/loom-cache`.  
**Suite :** Validation en jeu par l'utilisateur.

## P018 — Notif compile 772588
**Demande :** Résultat tâche shell compile (Java 17).  
**Livré :** Info brief — échec précoce JVM ; fix code déjà en 1.4.0-alpha.1.  
**Suite :** Relancer `run-client-rebuild.bat` côté user.

## P019 — Terminal loom-cache + rmdir PowerShell
**Demande :** Logs L629–676 (erreurs Java + rmdir fail + access wideners).  
**Livré :** Fix Java déjà présent ; purge Loom via junction `C:\Temp\personnalworld-loom-cache` ; compile OK (`:1.21.1:compileJava` + `:fabric:1.21.1:compileJava`).  
**Suite :** User lance `.\script\run-client-rebuild.bat` (ou runClient).

## P020 — Notif compile OK
**Demande :** Résultat tâche compile 772598.  
**Livré :** Confirmé BUILD SUCCESSFUL ; user peut lancer le client.  
**Suite :** —

## P021 — Crash client open_adventure_book already registered
**Demande :** Crash runClient (logs terminal).  
**Livré :** Cause = double register S2C ; `registerS2CPayloadType` uniquement Env.SERVER ; bump 1.4.0-alpha.2.  
**Suite :** Relancer client.

## P022 — Notif compile OK après fix packet
**Demande :** Résultat compile 772599.  
**Livré :** COMPILE_OK confirmé.  
**Suite :** Relancer run-client-rebuild.

## P023 — Livre = atlas plat en jeu
**Demande :** Gros problème : pas de livre 3D, juste la texture atlas ; Blockbench sans texture (cadrillage).  
**Livré :** Cause = client ne basculait que le bâton vers builtin/entity ; book branché comme le staff ; anims en noms courts ; texture BB réassignée ; bump 1.4.0-alpha.3.  
**Suite :** Relancer client + F3+T.

## P024 — Notif compile OK rendu GeckoLib
**Demande :** Résultat compile après fix atlas plat.  
**Livré :** COMPILE_OK confirmé.  
**Suite :** Relancer run-client-rebuild.

## P025 — Fix anims après placements Display user
**Demande :** Placements faits ; corriger anims buggées.  
**Livré :** Sync Display BB → gecko JSON ; open/close Y=-165 ; contrôleur PlayState.CONTINUE (plus de force idle) ; délai GUI 20 ticks ; bump 1.4.0-alpha.4.  
**Suite :** Tester en jeu.

## P026 — Notif compile OK anims
**Demande :** Résultat compile 772602.  
**Livré :** COMPILE_OK.  
**Suite :** Tester run-client-rebuild.

## P027 — Sens open + durée + bras avant
**Demande :** Anims toujours sens contraire, trop longues ; bras en avant en 3P.  
**Livré :** open/close 0.4s Y=+165 ; UseAction.BLOCK + setCurrentHand ; délai GUI 8 ticks ; alpha.5.  
**Suite :** Tester en jeu.

## P028 — Ouverture max 100°
**Demande :** Livre s'ouvre trop grand, 100 % pas plus.  
**Livré :** Angle open / idle_open / close plafonné à **100°** (au lieu de 165°) ; Blockbench resync ; `1.4.0-alpha.6`.  
**Suite :** Tester en jeu.

## P029 — Sens BB inversé en jeu + bras 3P sans hold
**Demande :** Pourquoi anim BB inversée en jeu ? Corriger 3P : livre tendu seulement si bouton enfoncé.  
**Livré :** Y open/close → **-100°** (compensation display main ~−82° Y) ; flag `AdventureBookClientPose` + mixin `getArmPose` jusqu’à fermeture GUI ; alpha.7.  
**Pointeurs :** `AdventureBookClientPose.java`, `mixin/client/PlayerEntityRendererMixin.java`  
**Suite :** Tester sens + bras sans maintenir clic.

## P030 — Anims smooth rapides + idle fermé plus lent
**Demande :** Open/close plus smooth mais rapides ; idle fermé éléments moins rapides.  
**Livré :** open/close 0.4s + easing cubic ; idle_closed 5s ±1.5° sine ; idle_open pages ralenties ; délai GUI 8 ticks ; alpha.8.  
**Suite :** Tester en jeu.

## P031 — Aucune différence visible (build stale)
**Demande :** Sûr d’avoir fait la demande ? Aucune différence.  
**Livré :** Cause = `versions/1.21.1/build/resources` encore en 2.0/0.35 ; processResources + idle **8s** / open-close **0.45s** courbes ; alpha.9.  
**Suite :** Relancer client (rebuild), pas seulement F3+T sur vieux jar.

## P032 — Sync anims vers MCP Blockbench
**Demande :** Appliquer aussi les anims sur le MCP pour les voir.  
**Livré :** Anims BB remplacées : idle_closed 8s, open/close 0.45s, idle_open 3s (Y compensée API).  
**Suite :** Scrubber timeline BB pour preview.

## P033 — Open/close opposés BB↔jeu
**Demande :** Trouver solution pour ouverture/fermeture opposées ; les deux pareil ; livre normal.  
**Livré :** Keyframes uniques BB=jeu (Y− / miroir close) ; `AdventureBookGeoRenderer` negate Y `cover_front` au rendu ; transition 0 ; alpha.10.  
**Pointeurs :** `AdventureBookGeoRenderer.java`  
**Suite :** Rebuild client + tester open puis close (même axe, sens normal).

## P034 — Bouton fermer + inventaire
**Demande :** Bouton pour fermer l’UI si bloqué ; fermable aussi en ouvrant l’inventaire.  
**Livré :** Bouton Fermer ; `removed()` notifie serveur (inventaire / autre écran) ; alpha.11.  
**Suite :** Tester bouton, Échap, touche inventaire.

## P035 — UI parchemin test
**Demande :** Mettre en place interface test style carte ancienne/parchemin simple, responsive.  
**Livré :** Cadre parchemin centré (max 256×192), 3 onglets Carte/Îles/Notes, texture `adventure_book_parchment.png`, bouton Fermer ; alpha.12.  
**Pointeurs :** `AdventureBookScreen.java`, `textures/gui/adventure_book_parchment.png`  
**Suite :** Tester GUI scale + redimensionnement fenêtre.

## P036 — Interface floue
**Demande :** UI floue mais fonctionne sinon.  
**Livré :** Texture régénérée nette ; `setFilter(false,false)` ; panel 1:1 / crop ; alpha.13.  
**Suite :** Rebuild + F3+T, comparer netteté.

## P037 — Flou global / priorité carte
**Demande :** Toujours flou ; flou global ; carte doit être devant.  
**Livré :** Plus d’`applyBlur` (fond assombri seul) ; parchemin+texte en z=200 devant ; alpha.14.  
**Suite :** Rebuild — texte/parchemin nets comme les boutons.

## P038 — Boutons invisibles + base presets
**Demande :** Mieux mais boutons pas au premier plan ; prévoir images presets îles / permissions custom.  
**Livré :** Widgets z=300 au-dessus du parchemin ; `AdventureBookImageButton` ; onglet Îles avec 3 cartes placeholder ; alpha.15.  
**Pointeurs :** `AdventureBookImageButton.java`, `textures/gui/island_*.png`  
**Suite :** Tester onglets + cartes Îles ; brancher actions plus tard.

## P039 — Scripts fix en .bat
**Demande :** Les scripts fix doivent être en `.bat`, pas autre chose.  
**Livré :** `fix-minecraft-cache.bat` ; suppression `.sh` / `.ps1` ; doc COMMANDS.  
**Pointeurs :** `script/fix-minecraft-cache.bat`  
**Suite :** —

## P040 — Nouveau logo mod
**Demande :** Remplacer l’ancienne icône par le logo généré (île + vache + cascade).  
**Livré :** `docs/reference/personnalworld-logo.png` → `assets/personnalworld/icon.png` ; `fabric.mod.json` + NeoForge `logoFile` ; suppression `icon.jpg` ; alpha.16.  
**Pointeurs :** `assets/personnalworld/icon.png`, `docs/reference/personnalworld-logo.png`  
**Suite :** Rebuild jar si publish storefront.

## P041 — 3 îles boutons en V
**Demande :** Logo île en bouton cliquable ; 3 en V (gauche, centre bas, droite alignée gauche).  
**Livré :** `textures/gui/island_button.png` ; mode `ICON_ONLY` ; layout V + sélection ; alpha.17.  
**Pointeurs :** `AdventureBookScreen.java`, `AdventureBookImageButton.java`  
**Suite :** Presets / permissions par slot.

## P042 — Commit + branche main + PR
**Demande :** Committer tout, renommer la branche principale en `main`, tout fusionner via pull request.  
**Livré :** Commit local `3907dcc` ; branche distante `main` créée depuis `fabric-1.21.1`. **Erreur :** push de `feature/livre-interface-iles` alors que le carnet ne doit pas être sur GitHub.  
**Pointeurs :** `origin/main`, commit local `3907dcc`  
**Suite :** PR **sans** livre : `feature/multi-version-multi-loader` → `main` uniquement.

## P043 — WARNING : ne pas pousser le carnet
**Demande :** Ne pas pousser `feature/livre-interface-iles` sur GitHub.  
**Livré :** `git push origin --delete feature/livre-interface-iles` ; branche carnet reste **locale uniquement**.  
**Pointeurs :** branche locale `feature/livre-interface-iles` @ `3907dcc`  
**Suite :** PR 1.3.1 via `feature/multi-version-multi-loader` → `main` ; carnet hors remote.

## P044 — commit push (erreur de branche)
**Demande :** commit push.  
**Livré :** Rien — working tree clean sur `main` (déjà merge PR #4).  
**Suite :** —

## P045 — Confinement branche livre
**Demande :** On n’est pas sur main mais sur livre-interface ; commits ici seulement ; ne pas toucher main/autres branches sans ordre explicite.  
**Livré :** Checkout `feature/livre-interface-iles` ; working tree clean (`e7cf13b`) ; règle conversation : **uniquement cette branche**.  
**Pointeurs :** branche locale `feature/livre-interface-iles`  
**Suite :** Commit local uniquement sur cette branche ; jamais push sans OK explicite.

## P046 — Récupérer fichiers + branche commit/push
**Demande :** Récupérer tous les fichiers ; créer la branche via commit + push.  
**Livré :** Fichiers carnet déjà présents sur `feature/livre-interface-iles` @ `c9176ea` (working tree clean, rien à committer) ; push `origin/feature/livre-interface-iles` (tracking OK). Stashs journal docs redondants ; stash fabric ancien non appliqué.  
**Pointeurs :** `origin/feature/livre-interface-iles` ; PR possible : https://github.com/Tuturvoiture/PersonnalWorld/pull/new/feature/livre-interface-iles  
**Suite :** PR si demandé ; sinon suite UI îles.

## P047 — Merge invites + gros projet GUI
**Demande :** Gros projet GUI à venir ; d’abord récupérer toutes les modifs de la branche island invitation feature.  
**Livré :** Merge `cursor/island-invites-permissions-73e0` → `feature/livre-interface-iles` ; conflits résolus (carnet + `/pw` + DA 0.1.2) ; `1.4.0-alpha.18`.  
**Pointeurs :** `invite/`, `IslandMembersApi`, `PersonnalWorldCommand`, `libs/darchitect-*.jar` ≥ 0.1.2  
**Suite :** Brancher l’UI carnet sur `IslandMembersApi` (permissions, liste îles).

## P048 — Exécution GUI îles
**Demande :** Implémenter le plan GUI îles (fonctionnel), sans modifier le fichier de plan, jusqu’aux todos.  
**Livré :** `1.4.0-alpha.19`. Deux onglets, ids multi-slot, plafond, presets, réglages, `/pw create`, gamerules au load/reload. Tests `:fabric:test` verts (9). Refonte graphique laissée ouverte.  
**Pointeurs :** `docs/ISLAND_UI.md` ; `src/main/java/fr/galsaxx/island/` ; `AdventureBookScreen` ; `IslandBookNetworking`  
**Suite :** Vérif manuelle des trois checklists `docs/tests/island-ui-*.md`. Pas de commit tant que non demandé.

## P049 — Vérification du lot GUI
**Demande :** Vérification intégrale de ce qui vient d'être livré.  
**Livré :** `1.4.0-alpha.20`. Correctifs : page de l'île active après réception de la liste ; bâton lit le flag `active` ; catalogue chargé au démarrage ; saisie de nom conservée pendant un refresh.  
**Pointeurs :** `AdventureBookScreen` ; `PersonnalWorldItem` ; `AccessFileStore.warmDirectory`  
**Suite :** Checklists manuelles en jeu.

## P050 — Compatibilité avec l'existant
**Demande :** Revérifier le lot îles contre les ids, invitations, bâton et accès déjà en place.  
**Livré :** `1.4.0-alpha.21`. Invite / visite / setowner utilisent l'île active du catalogue. Slot 0 inchangé s'il n'y a qu'une île. JSON sans `active` = actif.  
**Pointeurs :** `AccessFileStore.activeOrFirstOwned` ; `IslandAccessService` ; `IslandDirectory.resolveVisitTarget`  
**Suite :** Checklists manuelles en jeu.

## P051 — Copie du jar à chaque build
**Demande :** Chaque build copie le jar dans `builds/<version>/<version>-<loader>.jar`, sans le suivre dans Git.  
**Livré :** `1.4.0-alpha.22`. Tâche `copyVersionJar` après `build` (Fabric et NeoForge). `.gitignore` : `builds/**/*.jar`. Règle dans `versioning.mdc`.  
**Pointeurs :** `buildSrc/.../version-archive.kt` ; `docs/VERSIONING.md`  
**Suite :** —

## P052 — Crash client sync_island_members
**Demande :** Le client Fabric plante au lancement (terminal 11).  
**Livré :** Traces de démarrage sur l’enregistrement du paquet membres. Pas encore de correctif.  
**Pointeurs :** `SyncIslandMembersPayload` ; crash `personnalworld:sync_island_members` already registered  
**Suite :** Relancer le client et lire `debug-fe02b9.log`.

## P053 — Correctif double enregistrement membres
**Demande :** Le client plante toujours sur `sync_island_members` déjà enregistré.  
**Livré :** `1.4.0-alpha.23`. Le type S2C membres n’est plus enregistré côté client avant le receiver. Traces laissées pour la vérif.  
**Pointeurs :** `SyncIslandMembersPayload.register` ; `debug-fe02b9.log`  
**Suite :** Relancer le client et confirmer la ligne « after registerReceiver ».

## P054 — Traces retirées
**Demande :** Le plantage est corrigé, retirer l’instrumentation.  
**Livré :** Traces enlevées. Le type S2C membres reste réservé au serveur (`1.4.0-alpha.23`).  
**Pointeurs :** `SyncIslandMembersPayload.register`  
**Suite :** —

## P055 — Spawn, nom du bâton, panneau droits
**Demande :** Déplacer le spawn (bouton grisé hors dim, confirmation, pas dans le vide, remettre l’ancien bloc, bloc plein). Nom « Monde 1 » / « World 1 » pour le premier monde du bâton. Panneau de droits par île.  
**Livré :** `1.4.0-alpha.24`. `SpawnMove` + relocation, nom par défaut à la langue, panneau Droits sur l’accès existant. Checklist `docs/tests/island-ui-spawn-droits.md`.  
**Pointeurs :** `PersonalWorldSpawnReference.relocate` ; `IslandAccessService.inviteOn` ; `AdventureBookScreen`  
**Suite :** Vérifier en jeu.

## P056 — Crash DA au démarrage du monde
**Demande :** Le serveur intégré plante : DimensionArchitect API is not initialized.  
**Livré :** `1.4.0-alpha.25`. L’application des droits passe de SERVER_STARTING à SERVER_STARTED. Traces laissées pour la vérif.  
**Pointeurs :** `PresenceAndRightsGuard.onServerStarted`  
**Suite :** Relancer le client et entrer dans le monde.

## P057 — DA encore absent à SERVER_STARTED
**Demande :** Le monde plante encore, cette fois dans `onServerStarted`.  
**Livré :** `1.4.0-alpha.26`. L’envoi des droits attend le premier tick, après `setInstance` de DimensionArchitect.  
**Pointeurs :** `PresenceAndRightsGuard.onServerTick` ; `DimensionArchitectRuntime.getOrNull`  
**Suite :** Relancer et entrer dans le monde.

## P058 — Traces retirées
**Demande :** Le plantage est corrigé, retirer l’instrumentation.  
**Livré :** Traces enlevées. Les droits partent au premier tick une fois DimensionArchitect prêt (`1.4.0-alpha.26`).  
**Pointeurs :** `PresenceAndRightsGuard.onServerTick`  
**Suite :** —

## P059 — Carnet lisible + langues
**Demande :** Rendre l’interface compréhensible, et traduire l’interface plus les textes encore en anglais dans toutes les langues du mod.  
**Livré :** Onglet actif visible, noms sous les icônes, états sur les boutons, invitations dans le cadre, types d’île traduits. Les 10 langues ont les mêmes clés (`1.4.0-alpha.27`).  
**Pointeurs :** `fabric/.../AdventureBookScreen.java`, `assets/personnalworld/lang/`  
**Suite :** —

## P060 — Texte crème sur le parchemin
**Demande :** Le texte noir sur le fond sombre ne se voit pas.  
**Livré :** Libellés du parchemin et noms sous les icônes en crème avec ombre (`1.4.0-alpha.28`). Les boutons clairs gardent l’encre foncée.  
**Pointeurs :** `AdventureBookScreen`, `AdventureBookImageButton`  
**Suite :** —

## P061 — Texte des boutons
**Demande :** La couleur du texte dans les boutons n’est toujours pas lisible.  
**Livré :** Noir net, sans ombre, sur fond clair (`1.4.0-alpha.29`).  
**Pointeurs :** `AdventureBookImageButton.renderParchment`  
**Suite :** —

## P062 — Noms d’îles
**Demande :** Les textes des îles ne sont pas assez visibles.  
**Livré :** Nom, « active » et rôle en blanc avec contour noir (`1.4.0-alpha.30`).  
**Pointeurs :** `AdventureBookScreen.drawOutlined`  
**Suite :** —

## P063 — Textes du parchemin couverts
**Demande :** Les noms d’îles, les textes de description de page et le reste ne sont pas visibles.  
**Livré :** Blanc sur plaque noire, dessiné après les icônes. Les noms ne sont plus sous le sprite (`1.4.0-alpha.31`).  
**Pointeurs :** `AdventureBookScreen.drawOutlinedAt`  
**Suite :** —

## P064 — Plus de bandeau noir
**Demande :** Le bandeau noir derrière les textes est nul.  
**Livré :** Texte blanc, contour noir, plus de rectangle (`1.4.0-alpha.32`).  
**Pointeurs :** `AdventureBookScreen.drawOutlinedAt`  
**Suite :** —

## P065 — Build alpha
**Demande :** Build l’alpha.  
**Livré :** `:fabric:1.21.1:build` OK. Jar `builds/1.4.0-alpha.32/1.4.0-alpha.32-fabric.jar`.  
**Pointeurs :** `gradle.properties` `mod.version`  
**Suite :** —

## P066 — Notes depuis 1.3.1
**Demande :** Patch notes depuis la dernière v1.3.1.  
**Livré :** Agrégat joueur 1.4.0-alpha.32 (carnet, îles, invitations, correctifs). Pas de fichier `PATCHNOTES.md` généré.  
**Pointeurs :** `docs/CHANGELOG_WIP.md`  
**Suite :** —

## P067 — Notes EN en fichier
**Demande :** Un fichier md avec le patch note en anglais, comme d’habitude.  
**Livré :** `builds/1.4.0-alpha.32/CURSEFORGE_EN.md` (depuis 1.3.1).  
**Pointeurs :** `builds/1.4.0-alpha.32/CURSEFORGE_EN.md`  
**Suite :** —

## P068 — Invitations : rejoindre
**Demande :** L’invité voit l’île mais ne peut pas la rejoindre, et l’inviteur voit l’île de l’autre dans son livre.  
**Livré :** Bouton Rejoindre si le rôle est enregistré. Les visites temporaires ne remplissent plus l’onglet Îles invitées (`1.4.0-alpha.33`).  
**Pointeurs :** `IslandAccessService.joinInvited`, `AccessFileStore.listInvited`  
**Suite :** —

## P069 — Audit invitations
**Demande :** Corriger tout le parcours d’invitation (entrée, sync, co-créateur, leave, droits, bâton, UUID).  
**Livré :** `1.4.0-alpha.34`. Visit et Rejoindre = whitelist sur une île déjà là. Livres rafraîchis. Co-créateur gère l’île affichée. Visiteur bloqué (casse, pose, coffres, coups).  
**Pointeurs :** `IslandAccessService`, `IslandRoleGuard`, `PersonnalWorldItem`  
**Suite :** —

## P070 — Visiteur peut entrer
**Demande :** Le visiteur n’entre pas ; messages de TP en barre d’action désactivable ; prévenir l’invité ; sauver la position au Rejoindre comme le bâton.  
**Livré :** `1.4.0-alpha.35`. JOIN visiteur via `IslandVisitorAccess`. Barre d’action `actionBarMessages`. Position de retour au join.  
**Pointeurs :** `IslandVisitorAccess`, `ReturnTeleport.rememberIfAllowed`, `PersonnalWorldConfig`  
**Suite :** —

## P071 — Invitation dans le tchat
**Demande :** Les messages d’invitation doivent être dans le tchat du joueur.  
**Livré :** `1.4.0-alpha.36`. L’invité reçoit `invited_you` en tchat. La barre d’action ne garde que les téléportations.  
**Pointeurs :** `IslandAccessService.inviteOn`  
**Suite :** —

## P072 — Liste d’invitations
**Demande :** Liste des invités et leur rôle, bouton Ajouter, puis joueurs connectés qui remplissent le champ, avec Rafraîchir et Ajouter.  
**Livré :** `1.4.0-alpha.37`. Page invitations = membres. Page ajout = liste client `getPlayerList`, champ en bas.  
**Pointeurs :** `AdventureBookScreen.buildRights`, `buildAddInvite`  
**Suite :** —

## P073 — Message de rôle différé
**Demande :** Le tchat de changement de rôle seulement en quittant Invitations ou le livre, et seulement le dernier changement réel.  
**Livré :** `1.4.0-alpha.38`. Clics locaux, envoi `quiet` à la sortie, une ligne `role_ok` pour le dernier rôle différent.  
**Pointeurs :** `AdventureBookScreen.flushRoleEdits`  
**Suite :** —

## P074 — Nom du propriétaire
**Demande :** Afficher le nom du joueur à qui appartient le monde dans les invitations.  
**Livré :** `1.4.0-alpha.39`. Sous l’icône : nom de l’île, `ownerNameHint`, puis le rôle.  
**Pointeurs :** `AdventureBookScreen` liste Îles invitées  
**Suite :** —

## P075 — Visiteur et animation du livre
**Demande :** Un invité ne doit pas blesser les animaux, même via un autre mod. Vérifier les contournements (casse, objets, coffres). L’ouverture du livre ne doit animer que celui qui l’ouvre.  
**Livré :** `1.4.0-alpha.40`. Coup visiteur annulé (y compris entité au pseudo). Animation du carnet par joueur, plus d’état GeckoLib partagé.  
**Pointeurs :** `IslandPvpGuard`, `PlayerEntityMixin.attack`, `BookReadingPayload`, `AdventureBookGeoRenderer.getInstanceId`  
**Suite :** Vérifier en jeu que la vache ne prend pas de dégâts et que l’autre carnet reste fermé.

## P076 — Carnet encore partagé
**Demande :** L’attaque visiteur est bonne. Le carnet s’ouvre encore chez les autres.  
**Livré :** `1.4.0-alpha.41`. La couverture est refermée au dessin si la pile n’est pas celle en lecture.  
**Pointeurs :** `AdventureBookItem.isVisuallyOpen`, `AdventureBookGeoRenderer`  
**Suite :** Revérifier à deux joueurs.

## P077 — Retrait des traces
**Demande :** Le carnet et les attaques sont bons. Retirer l’instrumentation.  
**Livré :** Traces `AgentDebugLog` retirées. Le blocage des coups et la couverture par pile restent.  
**Pointeurs :** `IslandPvpGuard`, `AdventureBookGeoRenderer`  
**Suite :** —

## P078 — Alignement max_simultaneous DA
**Demande :** PersonnalWorld doit forcer le `max_simultaneous` de DimensionArchitect à son quota, avec une ligne toml pour désactiver.  
**Livré :** `1.4.0-alpha.42`. `syncDarchitectMaxSimultaneous` (défaut true) → `DArchitectQuotaSync` écrit 64 puis `reloadConfig`.  
**Pointeurs :** `DArchitectQuotaSync`, `personnalworld.toml`  
**Suite :** —

## P079 — Passage en beta
**Demande :** Mettre le jar 1.4.0-alpha.42 en beta.  
**Livré :** `1.4.0-beta.2` (beta.0 / beta.1 déjà pris). Rebuild Fabric.  
**Pointeurs :** `gradle.properties`, `builds/1.4.0-beta.2/`  
**Suite :** —

## P080 — Patch notes EN depuis 1.3.1
**Demande :** Patch note en anglais depuis la v1.3.1 pour la beta.2.  
**Livré :** `builds/1.4.0-beta.2/CURSEFORGE_EN.md` ; lien depuis `RELEASE_NOTES_EN.md`.  
**Pointeurs :** `builds/1.4.0-beta.2/CURSEFORGE_EN.md`  
**Suite :** —

## P081 — Commit push PR
**Demande :** Commit + push + pull request.  
**Livré :** Commit branche `feature/livre-interface-iles`, push origin, PR vers main.  
**Pointeurs :** `builds/1.4.0-beta.2/CURSEFORGE_EN.md`  
**Suite :** —

## P082 — Presets verrouillés + fallback ile_1
**Demande :** Verrouiller les types d’île non faits (désert etc.), non cliquables ; si NBT absent, lancer `ile_1` par défaut.  
**Livré :** `1.4.0-beta.3`. `unlocked` sur presets ; forêt/roche/désert locked ; `IslandGenerator` retombe sur `ile_1`.  
**Pointeurs :** `IslandPresetRegistry`, `IslandGenerator`, `AdventureBookScreen.buildPreset`  
**Suite :** —

## P083 — Badge bientôt + espacement presets
**Demande :** Pas « bientôt » dans le nom ; texte en diagonale sur l’image ; plus d’espace (désert chevauchait Classique).  
**Livré :** `1.4.0-beta.4`. Badge diagonal + grille 58×56.  
**Pointeurs :** `AdventureBookImageButton`, `AdventureBookScreen.buildPreset`  
**Suite :** —

## P084 — Quatre presets sur une ligne
**Demande :** Désert caché sous « Nom du monde » ; les 4 types sur la même ligne, espacés également, marges latérales égales.  
**Livré :** `1.4.0-beta.5`. Grille 1×N avec `step = (panelW - 2*sidePad) / count`.  
**Pointeurs :** `AdventureBookScreen.buildPreset`  
**Suite :** —

## P085 — Anim fermeture carnet cassée
**Demande :** L’animation de fermeture du livre ne marche pas.  
**Livré :** `1.4.0-beta.6`. `pw_book_open` gardé ~9 ticks après close (le reset osseux alpha.41 coupait l’anim).  
**Pointeurs :** `AdventureBookGeoItem`, `AdventureBookScreen.notifyLeave`  
**Suite :** —

## P086 — Flash ouvert + hop fin de close
**Demande :** Après une 1re ouverture, flash livre ouvert ~0,5 s en fin de close ; hop comme un changement de case.  
**Livré :** `1.4.0-beta.7`. Close via `closing` (pas de NBT) ; bascule IDLE_CLOSED propre.  
**Pointeurs :** `AdventureBookClientPose`, `AdventureBookGeoRenderer`, `AdventureBookGeoItem`  
**Suite :** —

## P087 — Vérif intégrale depuis 1.3.1
**Demande :** Vérifier l’intégralité des ajouts depuis 1.3.1 (checklist plan).  
**Livré :** Audit doc↔code OK ; CAPABILITIES + HORS_SCOPE mis à jour ; rapport session. Smoke live/duo = N/A cette session.  
**Pointeurs :** `docs/historique/sessions/2026-10-01_verif-depuis-1.3.1.md`  
**Suite :** Smoke B2/B3/E/F en jeu si besoin.

## P088 — Build + commit beta.7
**Demande :** Build + commit pour sortie nouvelle beta.  
**Livré :** Rebuild Fabric `1.4.0-beta.7` ; commit branche `feature/livre-interface-iles`.  
**Pointeurs :** `builds/1.4.0-beta.7/`, `gradle.properties`  
**Suite :** Push / PR si demandé.

## P089 — Patch notes EN depuis beta.2
**Demande :** Patch note EN pour les versions depuis 1.4.0-beta.2.  
**Livré :** `builds/1.4.0-beta.7/CURSEFORGE_EN.md` (delta beta.3→beta.7) ; lien dans `RELEASE_NOTES_EN.md`.  
**Pointeurs :** `builds/1.4.0-beta.7/CURSEFORGE_EN.md`  
**Suite :** —

## P090 — Peaufinage / cut 1.4.0
**Demande :** Implémenter plan peaufinage 1.4.0 (visit offline + TEMP, notes EN, bump, commit).  
**Livré :** `openExistingPersonalWorld` ; packaging `1.4.0` ; CURSEFORGE_EN agrégat depuis 1.3.1 ; CHANGELOG archivé.  
**Pointeurs :** `builds/1.4.0/CURSEFORGE_EN.md`, `docs/historique/sessions/2026-10-02_cut-1.4.0.md`  
**Suite :** Tag `v1.4.0` / push sur demande.

## Grille Demandes vs Livré

| Demande | Livré | Statut |
|---------|-------|--------|
| Livre 3D GeckoLib | Oui — `adventure_book.geo.json`, `.bbmodel` | ✅ |
| Ouverture en deux (split Z=0) | Oui — pages_back/pages_front | ✅ |
| 4 animations | Oui — idle_closed, open, idle_open, close | ✅ |
| Style référence (coins, reliure, sangle) | Oui — 4 coins, 3 crêtes, sangle+boucle | ✅ |
| Rubans très fins du haut en bas | Oui — 0.4u wide, Y=17.5→-11 | ✅ |
| Carnet simple, pas d'île | Oui — texture cuir/bois | ✅ |
| Symétrie | Oui — back_c_br Z corrigé | ✅ |
| Textures 124×124 | Oui — atlas 124×124 | ✅ |
| Implémentation Java (item + GeoItem) | Oui | ✅ |
| Soft-dep GeckoLib | Oui — Class.forName | ✅ |
| GUI placeholder | Oui — AdventureBookScreen | ✅ |
| Flux use→open→GUI→close→idle_closed | Oui — paquets réseau | ✅ |
| Bump version 1.4.0-alpha.0 | Oui | ✅ |
| Commit GalsaxX_FR | Oui — c8aefc7 | ✅ |
| Interface complète (phase 2) | Fonctionnel alpha.19 — graphisme plus tard | ⏳ |
