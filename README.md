# Personnal World Mod

**Version : V1**
**Compatibilité : Minecraft 1.21.1 (Fabric)**

---

## 📦 Présentation

**Personnal World** est un mod Fabric qui permet à chaque joueur de posséder et d’explorer son propre monde personnel dans Minecraft !  
Grâce à un objet spécial (“Bâton du monde perso”), vous pouvez accéder à une dimension unique, entièrement à vous, pour construire, expérimenter, et inviter des amis (fonctionnalités à venir).

---

## ⚙️ Fonctionnalités principales

- **Un monde perso par joueur** : À la première utilisation du bâton, une dimension propre à votre compte Minecraft est créée.
- **Téléportation aller-retour** : Passez de l’Overworld, du Nether ou de l’End à votre monde perso, et retournez exactement à votre dernière position.
- **Sauvegarde automatique** : Votre position d’origine est sauvegardée à chaque téléportation, même si vous déconnectez ou quittez le jeu.
- **Commande de secours** : `/retourmonde` pour revenir à votre emplacement sauvegardé au cas où vous seriez bloqué ou après un bug.
- **Modèle 3D custom** : Le bâton bénéficie d’un modèle et d’une texture 3D personnalisés.
- **Nom d’item localisé** : L’objet s’affiche avec un nom propre dans la langue du jeu.

---

## 🪄 Utilisation

1. **Obtenir le Bâton du Monde Perso**
    - En créatif : `/give <joueur> personnalworld:personnal_world_item`
    - (Ou via les recettes si ajoutées plus tard)

2. **Aller dans votre monde perso**
    - Faites un **clic-droit** avec le bâton dans l’Overworld, Nether ou End.
    - Le monde perso est créé s’il n’existe pas, puis vous êtes téléporté au spawn de ce monde.

3. **Retour à votre emplacement d’origine**
    - Faites **clic-droit** avec le bâton dans votre monde perso pour retourner là où vous étiez avant (dimension, coordonnées et orientation).

4. **En cas de bug ou si bloqué**
    - Utilisez la commande `/retourmonde` pour revenir à votre position sauvegardée.

---

## 💾 Persistance

- **Les mondes persos et les positions de retour sont sauvegardés**, y compris après redémarrage du serveur/jeu.
- Toutes vos constructions restent présentes dans votre monde perso.

---

## 📝 Installation

1. **Téléchargez et installez** :
    - [Fabric Loader](https://fabricmc.net/use/)
    - [Fabric API](https://modrinth.com/mod/fabric-api)
    - [DimLib](https://modrinth.com/mod/dimlib)
2. **Placez le fichier `personnalworld-x.x.x.jar` dans le dossier `mods/`** de votre instance Minecraft.
3. **Placez également les dépendances (Fabric API, DimLib) dans le dossier `mods/`.**

---

## 🔗 Dépendances

- **Fabric Loader**
- **Fabric API**
- **DimLib**

---

## 🏗️ Personnalisation & avancées à venir

- Import de cartes personnalisées ou d’îles volantes en tant que base de monde perso.
- Système d’invitation d’amis, gestion des permissions dans le monde perso.
- Menu interactif pour gérer ses mondes, invitations, et choisir le type d’île.
- Génération avancée : choix de biomes, presets d’îles, sauvegardes multiples…
- Compatibilité accrue multi-joueurs et nouveaux modèles d’items.

---

## 🐞 Problèmes connus / Limites

- La commande `/retourmonde` ne doit être utilisée **que dans un monde perso** (sinon, elle agit comme un TP !).
- Si vous modifiez la structure du mod, attention à la persistance NBT des joueurs.
- Le modèle 3D peut nécessiter des ajustements d’orientation selon la version Minecraft.

---


## 📧 Support et suggestions

Vous pouvez :
- Créer un ticket GitHub

---

**Bon jeu et bonne construction dans votre monde perso !**
