# Spawn, nom par défaut, droits

À jouer en jeu. Les règles de déplacement (refus, restauration, un seul cube) sont aussi dans `IslandLogicTest`.

1. Créer une première île avec le bâton, client en français. Résultat : le carnet affiche « Monde 1 ». En anglais, « World 1 ».
2. Ouvrir les réglages de cette île depuis l’Overworld. Résultat : « Modifier le point de spawn » est grisé, avec l’info qu’il faut être dans l’île.
3. Entrer dans l’île, se poser sur de la pierre, confirmer le déplacement. Résultat : le cube est sous les pieds, l’ancien cube a disparu, le bloc qui était sous le cube est revenu.
4. Voler ou se placer au-dessus du vide, confirmer. Résultat : message, le cube ne bouge pas.
5. Se poser sur un coffre, une barrière ou une clôture, confirmer. Résultat : message, le cube ne bouge pas.
6. Déplacer le cube une deuxième fois. Résultat : un seul cube. Le bloc de l’étape 3 est à l’emplacement précédent, pas un deuxième cube.
7. Réglages → Droits. Inviter un pseudo connu en Visiteur, puis passer Builder, puis Retirer. Résultat : la liste suit le JSON d’accès de cette île. Le propriétaire n’apparaît pas comme membre retirable.
8. Ouvrir Droits d’une autre île à soi. Résultat : la liste est celle de cette île, pas de l’île active si elle est différente.
