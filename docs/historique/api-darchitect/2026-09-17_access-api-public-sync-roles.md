# P2 — Accès dims : façade publique + sync rôles pour surcouche invitations

- **Date :** 2026-09-17
- **Consommateur :** PersonnalWorld `1.4.0-alpha.0` (branche invitations / droits d’îles)
- **API visée :** DimensionArchitect **0.0.59** (`darchitect`)
- **Sévérité :** P2 — feature jouable côté liste/commandes, mais **enforcement build/interact + kick** fragiles sans contrat API clair
- **Statut côté PW :** contournement partiel via `AccessManagerImpl.INSTANCE` (package `impl`) + garde JOIN PersonnalWorld ; **pas** de sync complète des rôles au kick

Copier cette fiche dans le Cursor du dépôt **DimensionArchitectAPI**. Ne pas patcher l’API depuis le workspace PersonnalWorld.

---

## 1. Ce que PersonnalWorld veut faire

Modèle retenu (**option A**) :

| Couche | Rôle |
|--------|------|
| **PersonnalWorld** | Source de vérité métier : whitelist JSON hors îles (`<world>/personnalworld/access/`), rôles OWNER / CO_CREATOR / BUILDER / VISITOR / TEMP, commandes `/pw`, visiteurs temporaires (RAM), owner logique |
| **DimensionArchitect** | **Enforcer runtime** : `MANAGED` + rôles → refus JOIN / BUILD / INTERACT via les mixins / `DimensionAccessGuard` déjà existants |

Flux attendu :

1. Joueur invite / kick / change rôle → PW écrit le JSON (`revision++`) **puis** pousse les rôles vers DA.
2. Load dim / `reload-access` → PW relit le JSON (validé) **puis** resync DA (PW gagne).
3. TEMP : uniquement RAM PW ; au restart / leave → plus de JOIN côté PW ; DA ne doit pas laisser un GUEST orphelin durable.
4. Debug ops : transfert d’owner logique, reload access, reload île (unload/load si API dispo).

Mapping actuel PW → DA :

- OWNER → `OWNER`
- CO_CREATOR → `MODERATOR`
- BUILDER → `MEMBER`
- VISITOR / TEMP_VISITOR → `GUEST`
- BANNED → `BANNED` (enum prêt côté PW, **pas** de commandes ban encore)

---

## 2. Pourquoi on est bloqué / fragile aujourd’hui

### 2.1 Pas de façade publique `access()`

`DimensionArchitect` (0.0.59) expose `builder`, `listDimensions`, `events`, etc. — **pas** d’accès au `DimensionAccessManager`.

Runtime utilisable seulement via :

- `net.darchitect.impl.AccessManagerImpl.INSTANCE`

Conséquences :

- Couplage à un package **impl** (casse possible à chaque bump).
- Impossible de documenter un contrat stable pour les surcouches.

### 2.2 Sync kick / retrait de rôle incomplète

API utile mais mal exposée :

- `setRole(dim, uuid, role)` — ajoute / remplace **un** rôle
- `setRolesForDimension(dim, map)` — remplacement de carte (présent sur **impl**, pas sur l’interface publique `DimensionAccessManager`)
- **Pas** de `clearRole` / `removeRole` public

Sans remplacement **complet** de la map (ou clear explicite), un joueur kické côté PW peut **garder** `MEMBER` / `GUEST` en mémoire / snapshot DA → il peut encore **builder** alors que la whitelist PW dit non.

C’est le blocage fonctionnel principal pour finaliser l’option A proprement.

### 2.3 Mode / owner figés après `build()`

À la création, PW peut faire :

```text
builder(id).accessMode(MANAGED).owner(uuid).… .build()
```

Pour les dims **déjà** existantes (mondes pré-1.4, souvent défaut OPEN) :

- Pas d’API claire pour forcer `OPEN → MANAGED` + owner post-création.
- Migration soft PW = fichier JSON + `setRole`, mais le **mode** DA peut rester OPEN → sémantique d’enforcement différente.

### 2.4 Unload / load dim

`unload` / `load` existent en commandes / `DimensionLifecycleService` (impl).  
Pas d’API publique simple pour qu’une surcouche fasse un reload d’île serveur (expulse + unload + load) de façon fiable.

### 2.5 Double écriture snapshot

Les rôles vivent aussi dans `DimensionSnapshot` / metadata dim.  
Sans contrat « qui gagne au unload », DA peut réécrire un snapshot qui **réintroduit** d’anciens rôles après un kick PW, ou l’inverse au rebuild corruption.

---

## 3. Ce qui aiderait PersonnalWorld à corriger (côté PW + prérequis DA)

Dès que DA expose le contrat ci-dessous, PW pourra :

1. Remplacer `AccessManagerImpl.INSTANCE` par `DimensionArchitectRuntime.get().access()`.
2. À **chaque** mutation whitelist (invite / kick / role / setowner) et au load :
   - construire la map complète `{ owner→OWNER, members→… }` **sans** les joueurs retirés ;
   - appeler `setRolesForDimension(dimId, map)` (ou clear + set).
3. Au start serveur / leave dim : purge TEMP PW + resync map DA (plus de GUEST orphelin).
4. Sur dims legacy : `setAccessMode(MANAGED)` + `setOwner(logicalOwner)` une fois, flag `accessMigrated`.
5. `/pw debug reload-island` : unload/load public au lieu d’un best-effort commande.

**Contournement PW possible sans bump DA (limité) :**  
appeler quand même `AccessManagerImpl.setRolesForDimension` depuis PW (déjà en `impl`) pour sécuriser les kicks — **améliore le gameplay immédiat**, mais **ne retire pas** le couplage impl ni le problème OPEN legacy / unload.

---

## 4. Correctifs proposés dans DimensionArchitect

### 4.1 À ajouter (priorité)

| Priorité | Changement | Détail |
|----------|------------|--------|
| **P0** | `DimensionArchitect.access(): DimensionAccessManager` | Façade publique ; déprécier l’usage direct de `impl` |
| **P0** | Sur l’interface publique : `getRolesForDimension` + `setRolesForDimension` | Remplacement **atomique** de toute la carte de rôles d’une dim |
| **P0** | `clearRole(dimensionId, playerUuid)` ou équivalent | Retrait explicite sans devoir reposer toute la map (optionnel si setRoles complet est garanti) |
| **P1** | `setAccessMode(dimId, mode)` + `setOwner(dimId, uuid)` post-création | Migration soft des dims OPEN → MANAGED |
| **P1** | Doc contrat sync | Qui gagne entre appels runtime et snapshot metadata à l’unload ; id canonique `namespace:path` partout |
| **P2** | `unload(dimId)` / `load(dimId)` publics | Pour reload île ops sans passer par la CLI |
| **P2** | Preset matrice ou stabilité des défauts GUEST | GUEST = JOIN (+ INTERACT?) **sans** BUILD ; breaking change documenté si modifié |

### 4.2 Vérifs attendues (côté API)

1. Créer dim MANAGED + owner → sans rôle, JOIN refusé.
2. `setRolesForDimension` avec owner + 1 MEMBER → MEMBER build OK ; retirer MEMBER de la map → **plus** de BUILD (et plus de JOIN en MANAGED).
3. Unload puis load → rôles runtime = dernière map posée par l’API (pas d’anciens UUID fantômes).
4. `setAccessMode(OPEN→MANAGED)` sur dim existante → comportement JOIN aligné MANAGED.
5. Surcouche compile **sans** importer `net.darchitect.impl.*`.

### 4.3 Fichiers / zones API probables (indicatif)

- `net.darchitect.api.DimensionArchitect` — ajouter `access()`
- `net.darchitect.access.DimensionAccessManager` — enrichir l’interface
- `net.darchitect.impl.AccessManagerImpl` — reste l’impl, plus le point d’entrée
- Persistence snapshot / unload — aligner écriture rôles sur `setRolesForDimension`
- (P1) config / metadata pour mode + owner mutables
- (P2) lifecycle public wrap de `DimensionLifecycleService`

---

## 5. Ce qu’il ne faut **pas** changer (compat PersonnalWorld)

| Ne pas casser | Raison |
|---------------|--------|
| Ids `personnalworld:perso_<uuid>` / namespace géré | Résolution îles + fichiers access PW |
| Noms / sens des enums `DimensionRole`, `DimensionPermission` | Mapping rôles PW |
| `AccessMode.OPEN` / `MANAGED` (sémantique MANAGED stricte) | Création + sécurité |
| `builder().accessMode().owner().permissions().type().worldProfile().isolatePlayerData().build()` | `PersonnalWorldUtil.ensurePersonalWorld` |
| Mixins / garde BUILD & INTERACT basés sur `DimensionAccessGuard` | Sinon visiteurs builderont |
| `DimensionAlreadyExistsException`, `hasDimension`, get world après build | Création concurrente |
| `registerMod` + quotas | Limite dims |
| Défaut GUEST **sans** BUILD (sauf major documentée) | Distinction VISITOR / BUILDER |
| Format snapshot existant **sans** migrateur | Mondes déjà créés |

En résumé : **ajouter** une façade et des opérations de sync ; **ne pas** renommer ni retendre les enums / le builder actuel.

---

## 6. Hors-sujet (ne pas mélanger dans ce ticket API)

- UI livre / GUI accept-deny visit (PersonnalWorld).
- Multi-îles réelles / switch active bâton (PersonnalWorld).
- Commandes `/pw ban` / `/pw unban` (prévues plus tard côté PW ; rôle `BANNED` DA déjà OK).
- NeoForge publish PersonnalWorld.
- Changer le gameplay île NBT / spawn_marker.

---

## 7. Contournement PW actuel (référence)

- Fichiers : `<worldSave>/personnalworld/access/*.json` (+ `corrupt/` si invalide).
- Code : `fr.galsaxx.invite.*`, wrap `DArchitectAccess` → `AccessManagerImpl.INSTANCE`.
- Garde JOIN : `PresenceAndRightsGuard` (tick) — **ne remplace pas** l’enforcement BUILD DA.
- TODO attendu après bump API : resync `setRolesForDimension` à chaque mutation + drop import `impl`.

---

## 8. Versions / jars

- Consommateur : PersonnalWorld `1.4.0-alpha.0`
- API testée : jars `libs/darchitect-fabric.jar` / `darchitect-neoforge.jar` **≥ 0.0.59**
- Bump API souhaité : documenter dans PATCHNOTES (ex. `0.0.60`) les ajouts `access()` + sync rôles **sans** breaking enums
