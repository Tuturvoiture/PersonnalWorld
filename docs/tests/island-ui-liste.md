# Checklist — liste des îles

Actions et résultats attendus. Pas besoin de lire le code.

1. Ouvrir le carnet avec 0 île. Résultat : onglet Mes îles, libellé `1/1`, bouton Créer visible, Précédent / Suivant / Aller ne changent pas la page.
2. Créer 1 île. Résultat : une icône, nom dessous, page `1/1`, centre plus bas que les côtés (V).
3. Avoir 4 îles. Résultat : page 1 montre 3 icônes en V ; page 2 montre 1 icône en Λ (centre plus haut) ; libellé `1/2` puis `2/2`. Le 4e emplacement de la page 1 n’existe pas et n’est pas cliquable.
4. Saisir `0` ou `99` dans Aller. Résultat : la page ne change pas.
5. L’île active est la 4e. Rouvrir le carnet. Résultat : ouverture sur la page 2. Changer de page puis rouvrir le même écran ne ramène pas de force sur l’active.
6. Atteindre `maxIslandsPerPlayer` (défaut 3). Résultat : Créer désactivé. Un essai refusé affiche « Nombre maximum d'îles atteint. » et aucune nouvelle dimension.
7. Être seulement membre d’une île. Résultat : absente de Mes îles, présente dans Îles invitées, avec rôle traduit (co-créateur, builder, visiteur ou invité temporaire) et pseudo du owner. Pas de bouton Créer sur cet onglet.
8. Mettre Mes îles sur la page 2 et Îles invitées sur la page 1, changer d’onglet deux fois. Résultat : chaque onglet retrouve sa page.
9. Cliquer une île invitée. Résultat : fiche lecture seule (nom, rôle, pseudo). Aucun réglage, aucun paquet de modification.
10. Le owner change de pseudo, se reconnecte, un invité rouvre le carnet. Résultat : la ligne propriétaire montre le nouveau pseudo.
