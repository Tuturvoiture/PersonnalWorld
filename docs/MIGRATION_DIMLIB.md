# Migration manuelle DimLib → 1.3 (DArchitect)

PersonnalWorld **1.3** ne reprend **pas** automatiquement les mondes perso créés avec **DimLib** (1.2.x). Il n’y a pas de migrateur intégré.

**Méthode recommandée :** créer un **monde perso neuf** sous 1.3, puis **y recopier** vos constructions depuis l’ancien save.

## Pourquoi cette méthode

| Approche | Risque |
|----------|--------|
| Recréer + copier (recommandé) | Contrôlée : la dim 1.3 est valide (DArchitect, île, `spawn_marker`, inventaire partagé) |
| Réutiliser / coller le dossier dim DimLib tel quel | Élevé : registre DArchitect, layout disque, état PW — peut créer une dim vide ou corrompre le save |

Les IDs restent du type `personnalworld:perso_<uuid>` (même UUID joueur), mais DimLib et DArchitect ne gèrent pas le cycle de vie de la même façon.

## Prérequis

- Minecraft **1.21.1**
- PersonnalWorld **1.3.x**
- **Dimension Architect** (`darchitect`) **≥ 0.0.59**
- Backup du save **avant** toute manipulation

## Procédure

### 1. Backup

1. Arrêtez le client / serveur.
2. Copiez tout le dossier du monde (ex. `.minecraft/saves/<NomDuMonde>/`) dans un dossier hors Minecraft.
3. Notez votre UUID joueur (profil Mojang / logs) : la dim s’appelle `perso_<uuid>`.

### 2. Installer la stack 1.3

1. Retirez DimLib des `mods/` s’il est encore présent.
2. Installez PersonnalWorld 1.3 + DArchitect ≥ 0.0.59 (+ Fabric API / Architectury, etc.).
3. Relancez le monde (ou une **copie** du save pour tester).

### 3. Créer le monde perso neuf

1. Obtenez le bâton (`personnalworld:personnal_world_item`).
2. Clic-droit dans l’Overworld / Nether / End.
3. Une dimension DArchitect VOID est créée, l’île NBT est posée, le `spawn_marker` est placé (Y=88, pieds Y=89).

Vous avez maintenant une dim **propre** 1.3. Ne vous fiez pas à l’ancienne dim DimLib comme « monde officiel ».

### 4. Copier les anciennes constructions

Depuis le **backup** (ou une instance 1.2.2 encore jouable) vers le **nouveau** monde perso :

**Option A — Structure / schematic (préférée)**

1. Sur l’ancien monde : sélectionnez vos builds (WorldEdit, Litematica, Structure Block, etc.).
2. Exportez (`.schem`, `.litematic`, structure vanilla, etc.).
3. Dans le nouveau monde perso 1.3 : collez / chargez la structure à l’endroit voulu (souvent près de `0, 90, 0`).

**Option B — Copie manuelle en jeu**

1. Ouvrez l’ancien save en 1.2.2 (ou un backup monté) pour photographier / lister ce qui compte.
2. Reconstruisez ou recollez pièce par pièce dans la dim 1.3.

**À éviter sans expertise**

- Remplacer aveuglément le dossier `dimensions/.../perso_<uuid>/` d’une dim 1.3 par celui DimLib.
- Supprimer le backup avant d’avoir vérifié le collage.

### 5. Vérifications

- [ ] Backup intact quelque part
- [ ] DimLib absent des mods
- [ ] DArchitect ≥ 0.0.59 présent
- [ ] Nouveau monde perso accessible au bâton
- [ ] Spawn sûr (marqueur / sol) ; `/returnworld` OK
- [ ] Constructions collées / présentes
- [ ] Inventaire partagé Overworld ↔ perso (comportement 1.3)

Si quelque chose cloche : restaurez le backup et recommencez sur une **copie** du save.

## Limites assumées

- Pas d’import automatique des chunks DimLib.
- Positions de retour (`pw_returnPosition`) peuvent rester ; ce n’est pas le cœur de la migration.
- Ancienne île DimLib non reprise telle quelle : la nouvelle île 1.3 sert de base ; vous y ajoutez vos builds.
- Mondes déjà créés sous DArchitect 0.0.57 avec inventaire isolé : autre sujet — recréer la dim ou `/darchitect delete` puis recreer (voir notes WIP / BDD).

## Support

En cas de doute, gardez le backup et ouvrez un ticket avec : version PW, version DArchitect, loader (Fabric/NeoForge), et si vous avez encore un save 1.2.2.
