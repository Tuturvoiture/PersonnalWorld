# Checklist — presets et réglages

1. Depuis Mes îles, Créer. Résultat : les presets reçus du serveur (au moins classic, forest, rock). Pas de preset inventé côté client.
2. Ajouter `config/personnalworld/island_presets/un.json` (`id`, `name`, `icon`), redémarrer le serveur, rouvrir Créer. Résultat : le preset apparaît sans recompiler. Icône vide ou inconnue = bouton île habituel.
3. Modifier le nom sous l’icône, Valider. Résultat : l’île créée porte ce nom (`displayName`).
4. Annuler. Résultat : retour Mes îles, aucun nouveau fichier d’île.
5. Nom vide ou preset inconnu. Résultat : message, aucune dimension.
6. Ouvrir les réglages d’une île à soi. Résultat : nom éditable, coordonnées de spawn, bouton Modifier le point de spawn, météo grisée, trois toggles, Retour.
7. Modifier le point de spawn. Résultat : le cube est replacé via le placement code existant. Pas de nouvel item, pas de déplacement libre.
8. Survoler Météo. Résultat : tooltip « À venir ». Aucun changement serveur.
9. Basculer Terrain ou Feu. Résultat : la gamerule de l’île change tout de suite, y compris si elle diffère de l’Overworld. Recharger l’île garde cette valeur (surcouche).
10. Basculer PvP sur false. Résultat : deux joueurs sur l’île ne se font plus de dégâts. `true` rétablit les dégâts (la propriété globale `pvp` du serveur s’applique encore). La valeur reste après reload.
11. Rendre active une île qui ne l’est pas, fermer, utiliser le bâton. Résultat : le bâton ouvre cette île. Retour depuis les réglages retrouve la page Mes îles.
12. Depuis le compte d’un invité, tenter les mêmes actions (paquet ou écran). Résultat : refus, l’île du owner ne change pas.
