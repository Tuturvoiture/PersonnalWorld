# Personnal World Mod

**Version : 1.3.1**  
**Compatibilité : Minecraft 1.21.1 — Fabric**

---

## Présentation

**Personnal World** est une surcouche de [Dimension Architect](https://github.com/Tuturvoiture/DimensionArchitectAPI) : chaque joueur peut ouvrir son propre monde personnel.  
L’API crée la dimension vide ; ce mod y place l’île NBT, l’item, et le téléport aller-retour.

**Loader supporté pour cette ligne de version : Fabric uniquement.** NeoForge (et autres) viendront plus tard, une fois la version Fabric complète.

---

## Fonctionnalités principales

- **Un monde perso par joueur** : À la première utilisation du bâton, une dimension `personnalworld:perso_<uuid>` est demandée à DimensionArchitect (`WorldType.VOID`).
- **Téléportation aller-retour** : Depuis la plupart des dimensions vers votre monde perso, puis retour à la position sauvegardée (ou spawn joueur / Overworld si aucune).
- **Inventaire partagé** par défaut entre l’Overworld et le monde perso (configurable).
- **Sauvegarde automatique** de la position d’origine (sauf dims listées dans la config).
- **Commande de secours** : `/returnworld` (uniquement depuis votre monde perso).
- **Modèle 3D custom** ; **animations optionnelles** via [GeckoLib](https://modrinth.com/mod/geckolib).

---

## Utilisation

1. **Obtenir le Bâton du Monde Perso**
    - Créatif : `/give <joueur> personnalworld:personnal_world_item`
    - Survie : recette 3 bâtons + 1 bloc d’herbe.

2. **Aller dans votre monde perso** — clic-droit avec le bâton (hors monde perso).

3. **Retour** — clic-droit dans votre monde perso, ou `/returnworld`.

---

## Configuration serveur

Fichier créé au démarrage : `config/personnalworld.toml` (commentaires en anglais + exemples).

| Clé | Défaut | Effet |
|-----|--------|--------|
| `noDimensionSavePosition` | `[]` | Dims où la position de retour **n’est pas** sauvée |
| `noDimensionTeleport` | `[]` | Dims où le bâton **refuse** d’ouvrir le monde perso |
| `staffCooldownTicks` | `40` | Cooldown du bâton (20 ticks = 1 s) |
| `shareInventory` | `true` | Inventaire partagé Overworld ↔ monde perso à la **création** de la dim |

Redémarrer le serveur après édition. `shareInventory` ne change pas les dimensions déjà créées.

---

## Persistance

- Mondes persos et positions de retour sauvegardés après redémarrage.
- **1.3** : mondes DimLib non migrés automatiquement — [`docs/MIGRATION_DIMLIB.md`](docs/MIGRATION_DIMLIB.md).

---

## Installation

1. **Dépendances** (dossier `mods/`) :
    - [Fabric Loader](https://fabricmc.net/use/) **≥ 0.18.4**
    - [Fabric API](https://modrinth.com/mod/fabric-api) **0.116.x** (1.21.1)
    - [Architectury API](https://modrinth.com/mod/architectury-api) **≥ 13.0.8**
    - **Dimension Architect** (`darchitect`) **≥ 0.0.59** (jar Fabric) — [GitHub / builds](https://github.com/Tuturvoiture/DimensionArchitectAPI)
2. Placez `personnalworld-fabric-x.x.x.jar` dans `mods/`.
3. **Optionnel :** [GeckoLib](https://modrinth.com/mod/geckolib) **4.7.5.1** pour les animations du bâton.

Même set de jars côté **serveur dédié**.

---

## Dépendances

- **Fabric Loader** ≥ 0.18.4
- **Fabric API** ≥ 0.116.0 (recommandé `0.116.0+1.21.1`)
- **Architectury API** ≥ 13.0.8
- **Dimension Architect (`darchitect`)** ≥ 0.0.59
- **GeckoLib** 4.7.5.1 (*optionnel*)

---

## Problèmes connus / Limites

- `/returnworld` fonctionne **seulement** depuis votre monde perso (sinon message d’erreur, pas de TP).
- Passage DimLib (1.2) → DArchitect (1.3) : pas d’import auto — [`docs/MIGRATION_DIMLIB.md`](docs/MIGRATION_DIMLIB.md).

---

## Support

- Issues : [GitHub PersonnalWorld](https://github.com/Tuturvoiture/PersonnalWorld/issues)
- Pages : [Modrinth](https://modrinth.com/mod/personnal-world) · [CurseForge](https://www.curseforge.com/minecraft/mc-mods/personnal-world)

**Bon jeu et bonne construction dans votre monde perso !**
