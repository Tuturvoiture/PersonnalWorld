# Checklist — `/pw create`

Permission 2. La commande n’est pas derrière `enableDebugCommands`.

1. `/pw create <joueur hors ligne>`. Résultat : dimension `personnalworld:perso_<uuid>` (slot 0) dont l’owner est la cible, pas l’admin. Nom = nom de la cible. Preset `classic`.
2. Le bâton de l’admin. Résultat : il n’ouvre pas l’île de la cible. Il ouvre l’île active de l’admin, ou crée le slot 0 de l’admin s’il n’en a aucune.
3. `/pw create <même joueur> forest` alors qu’il a déjà une île. Résultat : `perso_<uuid>_1`, pas active. La première reste active.
4. `/pw create <joueur> inconnu`. Résultat : message preset inconnu, aucune nouvelle dimension.
5. Avec `maxIslandsPerPlayer = 3`, un 4e `/pw create` pour le même owner. Résultat : « Nombre maximum d'îles atteint. », aucun fichier de dimension en plus.
6. `maxIslandsPerPlayer = 0`, créer au-delà de 3. Résultat : création autorisée.
7. Console (pas un joueur) : `/pw create <joueur>`. Résultat : même création, message de retour sur la console, pas d’exception.
