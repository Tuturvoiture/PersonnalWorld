# État courant

- **mod.version :** `1.4.0-beta.2` (`gradle.properties`)
- **MC :** 1.21.1 — publication **Fabric seule** (NeoForge reporté)
- **Branche active :** `feature/livre-interface-iles` → `origin/feature/livre-interface-iles`
- **Jar :** non buildé (alpha en cours)
- **API :** DimensionArchitect `darchitect` ≥ **0.1.2** (`access()` public)
- **GeckoLib :** optionnel 4.7.5.1

## En cours — GUI îles fonctionnel (alpha.35)

- Visiteur : JOIN autorisé. Messages de TP : barre d’action (`actionBarMessages`). Invitation reçue : tchat. Rejoindre enregistre la position de retour comme le bâton.
- Deux onglets carnet : Mes îles / Îles invitées. Pseudo = `ownerNameHint`, rafraîchi au login.
- Ids `perso_<uuid>` et `perso_<uuid>_<index>`. Plafond TOML. Gamerules copiées au load/reload, surcouche prioritaire.
- Continuité : `docs/ISLAND_UI.md`

## Ouvert (ne pas redécouvrir)

- Refonte graphique du carnet : l’écran actuel n’est pas beau. Lot à part (rendu, textures, mise en page). Pas fait.
- Smoke multi-joueurs : kick → plus de build ; TEMP leave/restart ; visit hôte offline
- Unload/load public DA (0.1.3) pour `reload-island` complet
- Comportement **BANNED** (enum prêt) — plan futur
- Upload Modrinth/CurseForge 1.3.1 / cut tag — hors priorité GUI
- NeoForge / roadmap : pas maintenant
