# GUI îles fonctionnel (alpha.19)

## Objectif

Brancher le carnet sur de vraies îles : liste, presets, réglages, commande admin. Pas de refonte graphique.

## Fait

- Onglets Mes îles et Îles invitées, pages de 3, ids `perso_<uuid>` et `perso_<uuid>_<index>`.
- Plafond `maxIslandsPerPlayer`, `/pw create <joueur> [preset]`.
- Gamerules recopiées au chargement et au reload ; surcouche terrain / feu. PvP d’île = blocage des dégâts joueur (pas de gamerule `pvp` en 1.21.1).
- Tests purs `:fabric:test` (9, verts).

## Pointeurs

- `docs/ISLAND_UI.md`
- `docs/tests/island-ui-liste.md`, `island-ui-preset-reglages.md`, `island-ui-admin.md`
- `src/main/java/fr/galsaxx/island/`

## Suite

- Checklists manuelles en jeu.
- Refonte graphique du carnet, lot séparé.
