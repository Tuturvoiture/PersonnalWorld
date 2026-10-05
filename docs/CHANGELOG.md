# Changelog

Notes de version archivées (effet joueur). Les changements **non encore publiés** vivent dans [`CHANGELOG_WIP.md`](CHANGELOG_WIP.md).

Format : plus récent en haut. Chaque section correspond à une version mise en ligne (Modrinth / CurseForge / tag `vX.Y.Z`).

## [1.4.4] — 2026-10-04

Patch Fabric 1.4 (unload île active, spawn UX, tooltips). Notes EN : [`builds/1.4.4/CURSEFORGE_EN.md`](../builds/1.4.4/CURSEFORGE_EN.md).

- 1.4.4 — Messages spawn / carnet sans mention N/E/S/O (orientation toujours enregistrée en coulisse).
- 1.4.3 — Bouton « Changer le spawn » : message sous les pieds ; tooltips FR/EN sur les boutons du carnet.
- 1.4.2 — Messages d’île plus clairs (désactivation ≠ rechargement) ; bouton Réserve visible « bientôt ».
- 1.4.1 — Changer d’île active décharge les autres îles du propriétaire (éviction + unload) ; cooldown 2 min (`activeIslandSwitchCooldownSeconds`).
- 1.4.1 — Visiter / rejoindre une île non chargée est refusé : le propriétaire doit l’activer d’abord.
- 1.4.1 — Login ou présence sur une île inactive / sans droit : renvoi à la position sauvegardée.
- 1.4.1 — Réserve d’îles chargées documentée « à faire » (en plus de l’active) — pas de code.

## [1.4.0] — 2026-10-02

Sortie stable Fabric 1.4 (carnet, multi-îles, invitations / droits). Notes EN storefront : [`builds/1.4.0/CURSEFORGE_EN.md`](../builds/1.4.0/CURSEFORGE_EN.md).

- 1.4.0 — `/pw visit` recharge une île déjà créée même si la dim était déchargée (hôte offline) ; ne crée plus de monde vide. TEMP = RAM only (leave / quit / restart).
- 1.4.0-beta.7 — Fermeture du carnet : plus de flash ouvert ni de « hop » de resélection en fin d’anim (état close hors NBT).
- 1.4.0-beta.6 — Fermer le carnet rejoue l’animation de fermeture (le rendu ne forçait plus la couverture fermée trop tôt).
- 1.4.0-beta.5 — Les 4 types d’île du carnet sont sur une seule ligne, espacés également avec les mêmes marges à gauche et à droite.
- 1.4.0-beta.4 — Types verrouillés : « bientôt » en diagonale sur l’icône ; grille des presets plus espacée pour ne plus couper les noms.
- 1.4.0-beta.3 — Types d’île non prêts (forêt, roche, désert…) verrouillés dans le carnet. Si le NBT du type manque, génération avec `ile_1`.
- 1.4.0-beta.2 — Passage en beta : carnet d’îles, invitations, droits visiteur, alignement `max_simultaneous` DimensionArchitect (depuis 1.4.0-alpha.42).
- 1.4.0-alpha.42 — Au démarrage, PersonnalWorld aligne `max_simultaneous` de DimensionArchitect sur son quota (64). Désactivable : `syncDarchitectMaxSimultaneous = false` dans `personnalworld.toml`.
- 1.4.0-alpha.41 — Ouvrir le carnet n’ouvre plus les autres carnets : seule la pile en lecture change de forme.
- 1.4.0-alpha.40 — Un visiteur ne blesse plus les entités de l’île (animaux, projectiles, familiers, entité qui porte son pseudo). Ouvrir le carnet n’anime que le carnet de celui qui l’ouvre.
- 1.4.0-alpha.39 — Dans Îles invitées, le pseudo du propriétaire s’affiche sous le nom du monde.
- 1.4.0-alpha.38 — Changer un rôle dans le carnet n’écrit plus dans le tchat à chaque clic. Une seule ligne apparaît en quittant Invitations ou le livre, et seulement s’il y a eu un changement.
- 1.4.0-alpha.37 — Invitations : la liste montre les invités et leur rôle. Ajouter ouvre les joueurs connectés, un clic remplit le champ, puis Rafraîchir ou Ajouter.
- 1.4.0-alpha.36 — L’invitation reçue s’affiche dans le tchat. Les messages de téléportation restent dans la barre d’action.
- 1.4.0-alpha.35 — Un visiteur peut entrer sur l’île. Les messages de téléportation et l’avis d’invitation s’affichent dans la barre d’action (`actionBarMessages`, désactivable). Rejoindre une île enregistre la position comme le bâton.
- 1.4.0-alpha.34 — Rejoindre et `/pw visit` n’ouvrent qu’une île déjà créée, et seulement si vous êtes invité. Les deux livres se mettent à jour après une invitation, un retrait ou un changement de rôle.
- 1.4.0-alpha.34 — Un visiteur ne casse pas, ne pose pas, n’ouvre pas les coffres et ne frappe pas les joueurs. Le bâton, sur l’île de quelqu’un d’autre, ramène à la position d’origine.
- 1.4.0-alpha.34 — Un co-créateur gère les invitations de l’île affichée. Retirer et changer un rôle visent le joueur, pas un pseudo périmé.
- 1.4.0-alpha.33 — Une invitation ouvre un bouton Rejoindre. Le livre n’affiche plus l’île de quelqu’un d’autre après une simple visite.
- 1.4.0-alpha.32 — Les phrases du carnet restent blanches, sans bandeau noir : seul un contour les détache du parchemin.
- 1.4.0-alpha.31 — Tous les textes du parchemin (noms d’îles, page, consignes, réglages) sont blancs sur un fond noir, dessinés au-dessus des icônes.
- 1.4.0-alpha.30 — Les noms sous les îles sont blancs, entourés de noir, pour rester lisibles sur le parchemin.
- 1.4.0-alpha.29 — Le texte des boutons du carnet est noir, sans ombre, sur un fond clair.
- 1.4.0-alpha.28 — Les textes posés sur le parchemin (noms d’îles, consignes, spawn) sont crème avec une ombre, plus lisibles sur le fond sombre.
- 1.4.0-alpha.27 — Le carnet explique chaque écran (liste, réglages, invitations, types d’île). Les boutons montrent l’état (terrain, feu, PvP) et tiennent dans le cadre. Toutes les langues du mod couvrent l’interface et les messages qui restaient en anglais.
- 1.4.0-alpha.26 — Rejoindre un monde ne plante plus : les droits d’île sont envoyés à DimensionArchitect au premier tick, une fois son instance créée.
- 1.4.0-alpha.24 — Le cube de spawn se déplace sous les pieds (confirmation, bouton inactif hors de l’île, bloc plein seulement, l’ancien bloc revient). Le premier monde du bâton s’appelle Monde 1 / World 1. Le carnet a un panneau Droits par île.
- 1.4.0-alpha.23 — Le client démarre à nouveau : la liste des membres du carnet n’est plus enregistrée deux fois au lancement.
- 1.4.0-alpha.22 — Chaque build copie le jar vers `builds/<version>/<version>-<loader>.jar`. Ces jars ne sont pas suivis par Git.
- 1.4.0-alpha.21 — `/pw invite`, `visit` et le transfert de propriétaire suivent l'île active, pas seulement `perso_<uuid>`. Un JSON d'accès ancien sans champ `active` reste actif.
- 1.4.0-alpha.20 — Le carnet s'ouvre sur la page de l'île active une fois la liste reçue. Le bâton suit le flag `active` du JSON, y compris après redémarrage.
- 1.4.0-alpha.19 — Carnet : onglets Mes îles et Îles invitées (liste par 3, presets, réglages). Plafond `maxIslandsPerPlayer` avec message, aussi sur `/pw create`.
- 1.4.0-alpha.19 — Gamerules d’île recopiées depuis l’Overworld au chargement et au reload, sauf surcouche (terrain, feu). PvP d’île coupe les dégâts entre joueurs.
- 1.4.0-alpha.18 — Fusion invitations/droits + DA ≥ 0.1.2 + façade `IslandMembersApi` avec le carnet.
- 1.4.0-alpha.17–alpha.0 — Carnet d’aventurier (GeckoLib optionnel), GUI parchemin, animations open/close, icône mod.
- 1.4.0-beta.1 — (branche invites) PATCHNOTES auto pour chaque version sous `build/libs/` ; beta/stable = agrégat depuis le dernier même canal.
- 1.4.0-beta.0 — Première beta ligne 1.4 (invitations / droits îles, sync DA ≥ 0.1.2, cibles online/offline).
- 1.4.0-alpha.5 — (branche invites) Façade UI `IslandMembersApi` + sync S2C.
- 1.4.0-alpha.4 — (branche invites) Fix liaison visit TEMP ; `PlayerRef` offline.
- 1.4.0-alpha.3 — (branche invites) `/pw` invite/kick/role/visit offline.
- 1.4.0-alpha.1 — (branche invites) DArchitect ≥ 0.1.2 : sync rôles atomique ; migration MANAGED ; purge TEMP.
- 1.4.0-alpha.0 — (branche invites) Invitations / droits : `/pw` ; whitelist JSON ; debug setowner / reload-*.

## [1.3.1] — 2026-09-12

- Sortie publique Fabric : config `personnalworld.toml` documentée ; README/notes EN ; nettoyage jar ; depends resserrées.

## [1.3.0] — 2026-09-12

- Première release Fabric stable (ligne 1.3 DArchitect ; NeoForge reporté).
- Config `personnalworld.toml` ; retour monde renforcé ; init île synchrone avant TP.
- Alphas : bâton GeckoLib optionnel, `spawn_marker`, inventaire partagé, migration DimLib → DArchitect (breaking, pas de migrateur auto).
