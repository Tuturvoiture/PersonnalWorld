# Checklist — presets et réglages

1. Depuis Mes îles, Créer. Résultat : classic cliquable ; forêt / roche / désert visibles mais grisés (bientôt). Pas de preset inventé côté client.
2. Ajouter `config/personnalworld/island_presets/un.json` (`id`, `name`, `icon`, optionnel `structure`, `unlocked`), redémarrer le serveur, rouvrir Créer. Résultat : le preset apparaît sans recompiler. Icône vide ou inconnue = bouton île habituel.
3. Modifier le nom sous l’icône, Valider (classic). Résultat : l’île créée porte ce nom (`displayName`), NBT `ile_1`.
4. Annuler. Résultat : retour Mes îles, aucun nouveau fichier d’île.
5. Nom vide, preset inconnu, ou `/pw create … forest`. Résultat : message, aucune dimension.
6. Ouvrir les réglages d’une île à soi. Résultat : nom éditable, coordonnées de spawn, bouton Modifier le point de spawn, météo grisée, trois toggles, Retour.
7. Toggle Terrain / Feu / PvP. Résultat : le bouton montre oui ou non ; l’overlay JSON change ; PvP off empêche les dégâts joueur-joueur sur l’île.
8. Renommer. Résultat : le titre et la carte Mes îles suivent.
