# Interface îles (carnet)

Lot **fonctionnel**. L’écran réutilise le parchemin et `AdventureBookImageButton`. Pas de nouvelles textures.

## Où c’est

- Écran Fabric : `fabric/…/client/AdventureBookScreen.java` (`Screen` vanilla).
- Cache client : `IslandBookClient`.
- Règles pures (aucun `net.minecraft`) : `src/main/java/fr/galsaxx/island/` — pagination, plafond, presets, resolver gamerules.
- Réseau : `IslandBookNetworking` (S2C liste + détail, C2S actions). Réglages : owner seulement. Invitations de l’île affichée : owner ou co-créateur.
- Catalogue : JSON `<monde>/personnalworld/access/` déjà là. Pas de second magasin.

## Deux onglets

Même grille de 3, index de page **indépendant**.

- **Mes îles** : `ownerUuid` = joueur. Nom sous l’icône. Clic = réglages. Bouton Créer (désactivé au plafond). 0 île = page `1/1` et Créer visible.
- **Îles invitées** : membre whitelist (pas une visite temporaire), et pas owner. Pas de Créer. Sous l’icône : nom de l’île, pseudo du propriétaire, rôle. Clic = fiche avec **Rejoindre** (île déjà créée, rôle visiteur / builder / co-créateur). Un co-créateur a aussi **Invitations**. `/pw visit` suit la même règle : pas de création de dimension, pas d’accès pour un inconnu.

Le pseudo affiché est `ownerNameHint` (pas un nouveau champ). À la connexion, si le nom du profil diffère, le hint owner et les `nameHint` membres sont réécrits puis sauvés.

## Ids

- Slot 0 : `personnalworld:perso_<uuid>` (mondes déjà en jeu).
- Slot ≥ 1 : `personnalworld:perso_<uuid>_<index>`.
- L’UUID lu = 36 caractères après `perso_`.
- L’île active est le record `active`, sinon la première du catalogue. Le catalogue non vide n’invente pas un `perso_<uuid>` absent.
- Le bâton ouvre l’île active. S’il n’y a aucune île, il crée le slot 0. Sur l’île de quelqu’un d’autre, il ramène à la position d’origine sans enregistrer cette île. Un nom vide devient « Monde 1 » ou « World 1 » selon la langue du client (aussi à l’ouverture du carnet si le nom est encore vide).
- Première île d’un owner : active. Les suivantes : non.

## Pages

Taille toujours 3. `pageCount = max(1, ceil(n/3))` sur les îles **créées**, jamais sur le max. Libellé `2/5`. Précédent, suivant et saut ne font rien sur `1/1` ni hors `1..pageCount`. Page impaire : V (centre plus bas). Page paire : Λ (centre plus haut). Ouverture une fois sur la page de l’île active, puis la page n’est plus forcée. Slots vides non cliquables. Pas de scroll. Onglets Carte et Notes retirés.

## Presets

Défauts `classic`, `forest`, `rock` (structure `ile_1`, icône `textures/gui/island_button.png`). Fichiers supplémentaires : `config/personnalworld/island_presets/*.json` (`id`, `name`, `icon`), lus au moment de la création / du sync. Icône vide ou inconnue → `island_button`. Le client n’invente pas un preset absent du S2C. Le nom sous l’icône est éditable et devient `displayName`. Annuler ne crée rien. Nom vide ou preset inconnu : message, aucune dimension.

## Plafond

`maxIslandsPerPlayer` dans `personnalworld.toml`, défaut 3. `0` ou négatif = illimité. Un TOML déjà là sans la clé charge le défaut. Au plafond : message `message.personnalworld.pw.max_islands`, aucune création (UI et `/pw create`).

## Réglages (owner seulement)

Nom, spawn, **Déplacer le cube** (inactif hors de l’île, confirmation, bloc plein seulement). Météo affichée « bientôt ». Toggles Terrain / Feu / PvP avec oui ou non sur le bouton. **Invitations** : liste des invités (rôle cliquable, retirer). **Ajouter** ouvre les joueurs connectés : un clic remplit le champ du bas, **Rafraîchir** relit la liste, **Ajouter** envoie l’invitation. Retirer envoie l’UUID tout de suite. Le rôle se change dans la page sans message : au retour ou à la fermeture du livre, une seule ligne de tchat, et seulement si le rôle final diffère du rôle d’arrivée (le dernier joueur modifié). Le co-créateur ouvre ce panneau depuis la fiche Îles invitées, pas les autres réglages. Pas de ban. Retour conserve la page d’où l’on vient. Les types Classique / Forêt / Roche suivent la langue du client (`preset.personnalworld.*`).

Un visiteur entre sur l’île (JOIN), mais ne casse pas, ne pose pas, n’ouvre pas coffre / baril / shulker / coffre de l’Ender, et ne blesse aucune entité (joueurs, animaux, projectiles, familier, entité à son pseudo). Builder et co-créateur construisent. Rejoindre enregistre la position actuelle comme le bâton (`noDimensionSavePosition`). Les avis de téléportation passent dans la barre d’action ; `actionBarMessages = false` les cache. L’invitation reçue reste dans le tchat.

## Gamerules

Ordre : (1) Overworld, (2) dimension hors clés de surcouche, (3) surcouche dans le NBT `PWWorldState` (`islandProfile` déjà présent, pas le JSON access).

Si `syncGamerules` est vrai (défaut) et qu’une règle de (2) diffère de (1) sans être dans (3) : copier (1) dans (2). La surcouche gagne sur ses clés. Si `syncGamerules` est faux : pas de copie Overworld, surcouche quand même.

En 1.21.1, `pvp` n’est pas une gamerule de dimension (c’est une propriété globale du serveur). La clé de surcouche `pvp` est quand même stockée. `false` empêche les dégâts entre joueurs sur cette île. `mobGriefing` et `doFireTick` restent de vraies gamerules.

Application au démarrage, au chargement de la dimension, et à `reload-island`. Pas de tick périodique.

## Commande

`/pw create <joueur> [preset]` — permission 2, pas derrière `enableDebugCommands`. Cible via `PlayerRef` (hors ligne OK). Owner = UUID cible. Nom d’affichage = nom de la cible. Preset absent = `classic`.

## Plus tard — refonte graphique

L’écran actuel n’est pas beau. Ce lot ne le redessine pas. Un lot à part refera rendu, textures et mise en page.
