# Personnal World Mod

**Version : 1.3.0-beta.0**
**Compatibilité : Minecraft 1.21.1 (Fabric / NeoForge)**

---

## 📦 Présentation

**Personnal World** est une surcouche de [Dimension Architect](https://github.com/Tuturvoiture/DimensionArchitectAPI) : chaque joueur peut ouvrir son propre monde personnel.  
L’API crée la dimension vide ; ce mod y place l’île NBT, l’item, et le téléport aller-retour.

---

## ⚙️ Fonctionnalités principales

- **Un monde perso par joueur** : À la première utilisation du bâton, une dimension `personnalworld:perso_<uuid>` est demandée à DimensionArchitect (`WorldType.VOID`).
- **Téléportation aller-retour** : Passez de l’Overworld, du Nether ou de l’End à votre monde perso, et retournez exactement à votre dernière position.
- **Sauvegarde automatique** : Votre position d’origine est sauvegardée à chaque téléportation, même si vous déconnectez ou quittez le jeu.
- **Commande de secours** : `/returnworld` pour revenir à votre emplacement sauvegardé.
- **Modèle 3D custom** : Le bâton bénéficie d’un modèle et d’une texture 3D personnalisés.
- **Animations (optionnel)** : Si [GeckoLib](https://modrinth.com/mod/geckolib) est installé, le cube d’herbe tourne en idle et pulse au clic-droit. Sans GeckoLib, le bâton reste statique (pas de crash).
- **Nom d’item localisé** : L’objet s’affiche avec un nom propre dans la langue du jeu.

---

## 🪄 Utilisation

1. **Obtenir le Bâton du Monde Perso**
    - En créatif : `/give <joueur> personnalworld:personnal_world_item`
    - En survie : recette 3 bâtons + 1 bloc d’herbe.

2. **Aller dans votre monde perso**
    - Faites un **clic-droit** avec le bâton dans l’Overworld, Nether ou End.
    - Le monde perso est créé s’il n’existe pas, puis vous êtes téléporté au spawn de ce monde.

3. **Retour à votre emplacement d’origine**
    - Faites **clic-droit** avec le bâton dans votre monde perso pour retourner là où vous étiez avant (dimension, coordonnées et orientation).

4. **En cas de bug ou si bloqué**
    - Utilisez la commande `/returnworld` pour revenir à votre position sauvegardée.

---

## 💾 Persistance

- **Les mondes persos et les positions de retour sont sauvegardés**, y compris après redémarrage du serveur/jeu.
- Toutes vos constructions restent présentes dans votre monde perso.
- **Alpha 1.3** : les mondes DimLib ne sont **pas** migrés automatiquement. Pour conserver des builds : créer un monde perso neuf puis y recopier les constructions — voir [`docs/MIGRATION_DIMLIB.md`](docs/MIGRATION_DIMLIB.md).

---

## 📝 Installation

1. **Téléchargez et installez** :
    - [Fabric Loader](https://fabricmc.net/use/) **≥ 0.18.4** (ou NeoForge 21.1)
    - [Fabric API](https://modrinth.com/mod/fabric-api) (Fabric)
    - [Architectury API](https://modrinth.com/mod/architectury-api)
    - **Dimension Architect** (`darchitect`) **≥ 0.0.59** (jars `darchitect-fabric.jar` / `darchitect-neoforge.jar`)
2. **Placez le fichier `personnalworld-{fabric|neoforge}-x.x.x.jar` dans le dossier `mods/`** de votre instance Minecraft.
3. **Placez également les dépendances (Fabric API / Architectury / DArchitect) dans `mods/`.**
4. **Optionnel :** [GeckoLib](https://modrinth.com/mod/geckolib) **4.7.5.1** (1.21.1) dans `mods/` pour les animations du bâton. La 4.9.2 demande un Fabric Loader / Fabric API plus récents que ce projet.

---

## 🔗 Dépendances

- **Fabric Loader** ≥ 0.18.4 *ou* **NeoForge** 21.1
- **Fabric API** (Fabric)
- **Architectury API** ≥ 13.0.8
- **Dimension Architect (`darchitect`)** ≥ 0.0.59
- **GeckoLib** 4.7.5.1 (*optionnel* — animations du bâton)

---

## 🏗️ Personnalisation & avancées à venir

- Import de cartes personnalisées ou d’îles volantes en tant que base de monde perso.
- Système d’invitation d’amis, gestion des permissions dans le monde perso.
- Menu interactif pour gérer ses mondes, invitations, et choisir le type d’île.
- Génération avancée : choix de biomes, presets d’îles, sauvegardes multiples…
- Compatibilité accrue multi-joueurs et nouveaux modèles d’items.

---

## 🐞 Problèmes connus / Limites

- La commande `/returnworld` ne doit être utilisée **que dans un monde perso** (sinon, elle agit comme un TP !).
- Si vous modifiez la structure du mod, attention à la persistance NBT des joueurs.
- Le modèle 3D peut nécessiter des ajustements d’orientation selon la version Minecraft.
- Passage DimLib (1.2) → DArchitect (1.3) : pas d’import auto — [`docs/MIGRATION_DIMLIB.md`](docs/MIGRATION_DIMLIB.md).

---


## 📧 Support et suggestions

Vous pouvez :
- Créer un ticket GitHub

---

**Bon jeu et bonne construction dans votre monde perso !**
