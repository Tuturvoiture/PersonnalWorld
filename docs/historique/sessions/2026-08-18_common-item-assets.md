# Item, assets et commande dans le module commun

- **Date :** 2026-08-18
- **Version début / fin :** 1.3.0-alpha.7 → 1.3.0-alpha.8

## Prompt / objectif

Déplacer assets + item (et le gameplay associé) vers le module commun pour que NeoForge ait le bâton, le modèle 3D et `/returnworld`.

## Fait

- Assets (modèle, textures, lang, icon), recette et NBT île : `src/main/resources/`.
- Java gameplay (item, île, mixin retour, commande) déplacé dans `src/main/java/`.
- Enregistrement via Architectury (`DeferredRegister`, onglet Outils, `CommandRegistrationEvent`, `SERVER_STARTED`).
- Mixins communs référencés aussi par NeoForge (`neoforge.mods.toml`).
- Entrypoints Fabric / NeoForge n’appellent plus que `PersonnalWorld.init()`.

## Fichiers touchés

- `src/main/java/fr/galsaxx/PersonnalWorld.java`, `PersonnalWorldContent.java`, `PersonnalWorldItem.java`
- `src/main/java/fr/galsaxx/util/*`, `command/ReturnWorldCommand.java`, `mixin/PlayerEntityMixin.java`
- `src/main/resources/assets/`, `data/`, `personnalworld.mixins.json`
- `fabric/.../PersonnalWorldFabric.java` (retrait de `PersonnalWorldMod`)
- `neoforge/.../PersonnalWorldNeoForge.java`, `neoforge.mods.toml`

## Décisions

- Client mixins d’exemple et datagen restent Fabric-only.
- `TestCopieNBT` reste dans Fabric (outil de debug, pas du gameplay).

## Ouvert / à corriger

- Vérifier en jeu NeoForge : modèle du bâton + clic-droit + `/returnworld`.

## WIP

- 1.3.0-alpha.8 — item / assets / `/returnworld` communs Fabric et NeoForge.
