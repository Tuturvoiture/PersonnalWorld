# Kit nouveau projet Cursor

Fichier **unique** à copier dans n’importe quel dépôt (jeu, web, CLI, data, hardware, autre).  
Il n’est **pas** lié à Minecraft. Les modules domaine sont optionnels.

**Usage :** coller ce fichier à la racine du nouveau repo (ou dans `docs/`), remplir le bloc **Paramètres**, puis demander à l’agent : *« Applique le kit nouveau projet ». *

**Instance PersonnalWorld :** paramètres §2 remplis, profil jeu, modules Minecraft / API / assets / MCP cochés. Socle process appliqué (rules + extensions 8.1).

---

## 1. Comment l’utiliser

1. Copier ce markdown dans le nouveau projet.
2. Remplir **§2 Paramètres** (tout ce qui est entre `« »` ou laissé vide).
3. Cocher les **modules** (§7) et les **extensions** (§8) voulus.
4. Dire à l’agent d’appliquer le kit : il crée l’arbre, les rules, les docs vides, **sans** copier le code métier d’un autre repo.
5. Premier commit **seulement si tu le demandes**.

**Ne pas copier** depuis PersonnalWorld : Gradle Minecraft, Fabric/NeoForge, GeckoLib, DimensionArchitect, mixins, NBT, assets de jeu.

---

## 2. Paramètres — à remplir en premier

Remplacer les valeurs. L’agent s’en sert comme source de vérité.

```yaml
# --- Identité ---
nom:                 PersonnalWorld
slug:                personnalworld
description:         Un monde personnel (île) par joueur, via DimensionArchitect
langue:              fr
auteur_git:          GalsaxX_FR
email_git:           145108538+Tuturvoiture@users.noreply.github.com
licence:             LGPL-3.0

# --- Produit ---
type:                game
audience:            joueurs
plateformes:         Windows / Linux — Minecraft 1.21.1 Fabric + NeoForge
runtime:             JDK 21

# --- Versionnement ---
version_initiale:    1.3.0-alpha.14
fichier_version:     gradle.properties
variable_version:    mod.version
suffixe_build:       +1.21.1
majeure_sur_demande: true

# --- Git / release ---
branche_principale:  main
remote:              github
phrase_release:      "c'est en ligne"
tag_format:          v{version}
commit_sauf_demande: true

# --- Agent ---
reponses_en:         fr
lire_bdd_chaque_tour: false   # BDD seulement en tour d'implémentation
journal_prompts:     true
```

**Hors scope (1–5 lignes) — ce que ce projet n’est pas :**

- Pas un remplaçant de DimensionArchitect (surcouche seulement).
- Pas de migration des mondes DimLib (breaking 1.3).
- Pas d’upload auto Modrinth / CurseForge ; pas de majeure `2.0` sans demande explicite.
- Pas d’invitations / menu / types d’îles (roadmap, pas commencé).

**Dépendances / dépôts sœurs (optionnel) :**

| Rôle | Nom | Contrainte |
|------|-----|------------|
| API dimensions | DimensionArchitect (`darchitect`) | ne pas modifier depuis ce workspace |

---

## 3. Arbre à créer (socle)

Après remplissage, l’agent crée **uniquement** ce qui n’existe pas encore.

```
.
├── README.md
├── LICENSE                         # si licence choisie
├── .gitignore
├── .editorconfig                   # si module Editor
├── AGENTS.md                       # pont court vers les rules + kit
├── .cursor/
│   ├── rules/
│   │   ├── prompt-bdd.mdc          # always (lecture BDD conditionnelle)
│   │   ├── versioning.mdc          # always
│   │   ├── git-attribution.mdc     # always
│   │   ├── changelog-wip.mdc       # agent-requested
│   │   ├── session-history.mdc     # auto-attached (globs livraison)
│   │   ├── release-cut.mdc         # agent-requested
│   │   └── domaine-*.mdc           # globs / agent-requested
│   └── skills/                     # seulement si un skill métier est utile
├── docs/
│   ├── VERSIONING.md
│   ├── CHANGELOG.md
│   ├── CHANGELOG_WIP.md
│   ├── CAPABILITIES.md
│   ├── kit-nouveau-projet.md
│   └── historique/
│       ├── README.md
│       ├── bdd/
│       ├── prompts/
│       └── sessions/
└── « sources selon le type »
```

---

## 4. Règles Cursor — contenu générique

Chaque rule : **un sujet**, **< 80 lignes**, frontmatter YAML.  
Chemins ci-dessous à adapter avec `fichier_version` / `slug`.

**Scope de chargement — ne pas tout mettre en `alwaysApply: true`.** Chaque rule always coûte des tokens à *chaque* tour, même hors sujet. Répartir ainsi :

- **`alwaysApply: true`** : uniquement ce qui doit s'appliquer littéralement partout → `git-attribution.mdc`, `versioning.mdc`, `prompt-bdd.mdc` (lecture BDD conditionnelle).
- **Auto-attached par glob** : `session-history.mdc` (fichiers de livraison), rules domaine sur leurs globs.
- **Agent-requested** : `changelog-wip.mdc`, `release-cut.mdc`.

Les instructions restent des **consignes d'action**, pas des explications de contexte : pas de « pourquoi », seulement le « quoi faire ».

### 4.1 `git-attribution.mdc` (`alwaysApply: true`)

- Auteur = `auteur_git` + `email_git` (config **repo-locale** si besoin).
- Interdit dans commit / tag / PR : marque de l’assistant, `Co-authored-by:` bot, « Generated with Cursor ».
- Vérifier `user.name` / `user.email` **avant** chaque commit.
- Pas d’`amend` / rewrite juste pour « enlever l’assistant ».
- Ne pas toucher à `--global` sauf demande.

### 4.2 `versioning.mdc` (`alwaysApply: true`)

Pointer vers `docs/VERSIONING.md`. Après **chaque** livraison fonctionnelle :

1. Incrémenter la version dans `fichier_version` (pas ailleurs en dur).
2. Petit changement → patch (ou `alpha.N` / `beta.N`).
3. Gros ajout → mineure en alpha, puis beta, puis stable.
4. 1–3 bullets dans `CHANGELOG_WIP.md`.
5. Ne **pas** sauter de patch « pour plus tard ».
6. Majeure seulement si `majeure_sur_demande` et demande **explicite**.

### 4.3 `changelog-wip.mdc` (`alwaysApply: false` — agent-requested, déclenchée sur livraison / mention de version)

- WIP = changements non publiés, formulés côté effet utilisateur (pas de liste de fichiers).
- Pas de secrets ni chemins personnels.
- Vider le Pending **seulement** au cut release (`phrase_release`).

### 4.4 `session-history.mdc` (auto-attached — déclenchée sur commit / livraison fonctionnelle, pas always)

- Avant un correctif : lire `docs/historique/sessions/INDEX.md` + 1–2 fiches.
- Après une **livraison fonctionnelle** : `YYYY-MM-DD_slug.md` + ligne d’index.
- Template : date, versions début/fin, objectif, fait, fichiers, décisions, ouvert, bullets WIP.
- Interdit : secrets, transcripts complets.

### 4.5 `release-cut.mdc` (`alwaysApply: false` — agent-requested, déclenchée par `phrase_release`)

Quand l’utilisateur dit `phrase_release` (ou « release X.Y.Z publiée ») :

1. Pending WIP → nouvelle section en **tête** de `CHANGELOG.md`.
2. Mettre à jour « Dernière version en ligne » et **vider** Pending.
3. Mettre à jour `CAPABILITIES.md` si le produit a changé.
4. Figer l’archive de build de cette version (si le projet en a une).
5. Proposer le tag `tag_format` — **sans push** ni commit sauf demande.

### 4.6 `prompt-bdd.mdc` (`alwaysApply: true`, lecture BDD conditionnelle)

**Tour d'implémentation** (code à écrire/modifier) :

1. Lire `docs/historique/bdd/INDEX.md` puis les fiches « lire d’abord ».
2. Lire `docs/historique/prompts/INDEX.md` + le fichier de **cette** conversation.
3. Grep / sous-agents seulement si la BDD n’a pas le pointeur.

**Tour de conversation** (question, explication, échange sans modification de code) : ne pas relire la BDD par défaut — répondre directement, sauf si la question porte explicitement sur l'historique ou l'état du projet.

**Après chaque prompt utilisateur** (même sans code) : incrémenter `P00N` (demande, livré, pointeurs, suite).  
Fait réutilisable → `bdd/` (une phrase + un chemin).  
`prompts/` + `bdd/` = chaque message. `sessions/` + version + WIP = livraison fonctionnelle.

Slug = titre de conversation en kebab-case ASCII. Stable ; si le titre change, **renommer** le fichier + l’index.

**Seuil de taille :** si `etat-courant.md` ou `fichiers.md` dépasse ~40 lignes, archiver les entrées anciennes (vers `decisions.md` ou `docs/adr/` si ce module est activé) au lieu de laisser grossir le fichier lu à chaque tour d'implémentation.

### 4.7 `AGENTS.md` (racine)

10–20 lignes max : qui est le produit, où est le kit, quelles rules `always`, où est la version.

**Source unique de vérité :** chaque info ne doit vivre qu'à **un seul endroit** — soit dans une rule, soit dans `AGENTS.md`, jamais dans les deux. `AGENTS.md` **pointe** vers les rules, il ne les répète pas.

---

## 5. Docs socle — squelettes

### `docs/VERSIONING.md`

SemVer : `{major}.{minor}.{patch}` ; prerelease `{major}.{minor}.0-alpha.{N}` puis `-beta.{N}`.

| Cas | Action |
|-----|--------|
| Correctif / petit changement | patch ou `alpha.N+1` |
| Nouvelle feature | mineure en alpha |
| Stabilisation | beta → `X.Y.0` |
| Majeure | seulement demande explicite |

Sources de vérité : un **seul** `fichier_version` ; métadonnées packagées via interpolation du build, jamais hardcodées.

### `docs/CHANGELOG_WIP.md`

```markdown
# Changelog WIP

**Dernière version en ligne :** aucune

## Pending

- (vide au jour 0)
```

### `docs/CHANGELOG.md`

```markdown
# Changelog

Releases archivées. Le vivant est dans CHANGELOG_WIP.md.
```

### `docs/CAPABILITIES.md`

Liste **vérifiable** de ce que le produit fait **aujourd’hui** (pas la roadmap).  
Une puce = un comportement que l’utilisateur peut constater.

### `docs/historique/bdd/`

- `etat-courant.md` — version, branche, dettes ouvertes.
- `fichiers.md` — sujet → chemin (lire **avant** un grep).
- `decisions.md` — choix durables.
- Style : une phrase + un chemin. Pas de pavé, pas de copie de code.

### Journal de prompt

Un fichier par conversation Cursor. Incrémenter à **chaque** message.

---

## 6. Profils de projet (choisir 1)

Cocher **un** profil ; l’agent n’invente pas de stack.

| Profil | `type` | Extra typique |
|--------|--------|----------------|
| [ ] Application web | `web` | `app/` ou `src/`, routes, UI, tests e2e |
| [ ] API / backend | `api` | OpenAPI ou équiv., auth, migrations |
| [ ] CLI / outil | `cli` | `src/`, `--help`, codes de sortie |
| [ ] Bibliothèque | `lib` | API publique stable, exemples, semver strict |
| [ ] Extension éditeur | `editor` | `package.json` contribution points |
| [ ] Mobile | `mobile` | iOS/Android, stores, secrets signing **hors git** |
| [x] Jeu (non-MC ou MC) | `game` | assets, boucle de jeu — module Minecraft **à part** |
| [ ] Data / ML | `data` | notebooks isolés, datasets **non commités** si gros |
| [ ] Firmware / hardware | `hw` | schémas, pinout, versions toolchain |
| [ ] Infra / IaC | `infra` | environnements, secrets via vault, pas en clair |
| [ ] Autre | `autre` | décrire en 3 lignes sous Hors scope |

---

## 7. Modules domaine (opt-in)

Ne générer une rule / un dossier **que** si coché.

| Cocher | Module | Ce que ça ajoute |
|--------|--------|------------------|
| [ ] | **Web UI** | rule : vérifier le flux dans le navigateur ; états vide/erreur ; pas un seul screenshot |
| [x] | **API externe** | rule type DArchitect : bug amont → rapport `docs/historique/api-<nom>/`, pas de patch du dépôt sœur |
| [x] | **Minecraft** | Gradle, loaders, mixins, assets — **uniquement** si le projet EST un mod |
| [ ] | **Données perso / RGPD** | inventaire des données, conservation, pas de PII dans logs/docs |
| [ ] | **Secrets / clés** | `.env.example` sans valeurs ; interdiction de committer `.env`, tokens, keystores |
| [ ] | **CI** | un workflow minimal (lint + test) ; pas de release auto sans demande |
| [ ] | **Tests** | où vivent les tests, commande, « pas de merge/livraison si rouge » (si tu veux cette barre) |
| [ ] | **i18n** | fichiers de langue, chaîne UI jamais hardcodée sans clé |
| [ ] | **Accessibilité** | si UI : clavier, contrastes, labels |
| [ ] | **Observabilité** | logs structurés, pas de secret dans les logs |
| [x] | **MCP / outils** | liste des serveurs MCP utiles + fichier `docs/MCP_SETUP.md` |
| [x] | **Design / assets** | licence des assets, chemins, outil (Figma, Blockbench, …) |

---

## 8. Extensions proposées

Coche ce que tu veux **en plus** du socle. L’agent les crée au prochain « applique le kit » (ou tu les ajoutes plus tard).

### 8.1 Fortement recommandé (presque toujours)

| Cocher | Extension | Pourquoi |
|--------|-----------|----------|
| [x] | **`.gitignore` sérieux** | Build, IDE, OS, secrets, caches — dès le jour 0 |
| [x] | **`.env.example`** | Contrat de config **sans** secrets ; même pour un projet solo |
| [x] | **`docs/HORS_SCOPE.md`** | Empêche l’agent d’élargir tout seul (ex. « pas de cloud », « pas de compte utilisateur ») |
| [x] | **Definition of Done** | 5–8 cases : version bumpée, WIP, session, tests, pas de secret, docs capacités |
| [x] | **Commandes canoniques** | `docs/COMMANDS.md` ou un `justfile` / `Makefile` : build, test, run, lint — une seule façon |
| [x] | **Matrice d’environnements** | local / CI / prod : ce qui diffère (URL, flags) |
| [x] | **Politique secrets** | 10 lignes dans `SECURITY.md` : où vivent les clés, rotation, quoi faire si fuite |
| [x] | **`.cursorignore`** | Builds, `node_modules`, jars, datasets — sans lui Cursor indexe du bruit sur chaque recherche sémantique, coût dès le jour 0 |
| [x] | **Rules par glob plutôt que always** | Une rule always coûte des tokens à chaque tour même hors sujet ; réserver `alwaysApply` au strict minimum (git-attribution, versioning) |

### 8.2 Qualité & collab

| Cocher | Extension | Pourquoi |
|--------|-----------|----------|
| [ ] | **Templates PR / issues** | `.github/PULL_REQUEST_TEMPLATE.md` : résumé, plan de test, breaking ? |
| [ ] | **Convention de branches** | `feature/…`, `fix/…` ; pas de commit direct sur `main` si tu veux cette discipline |
| [ ] | **CODEOWNERS** | utile dès qu’il y a un 2e contributeur (humain ou bot CI) |
| [ ] | **Revue** | checklist : sécurité, perfs, accessibilité — ou skill `review-*` |
| [ ] | **CI minimale** | lint + tests à chaque push ; **pas** de publish automatique |
| [ ] | **Dependabot / renovate** | updates de deps ; tu valides, l’agent ne merge pas tout seul |
| [ ] | **EditorConfig + formatter** | `.editorconfig` + Prettier / ruff / rustfmt — évite les diffs de style |
| [ ] | **Hooks pre-commit** | format + secret scan ; **ne jamais** `--no-verify` sauf demande |

### 8.3 Produit & mémoire

| Cocher | Extension | Pourquoi |
|--------|-----------|----------|
| [ ] | **Roadmap courte** | `docs/ROADMAP.md` : maintenant / ensuite / plus tard — distinct de CAPABILITIES |
| [ ] | **Glossaire** | 10–20 termes du domaine (l’agent arrête d’inventer des synonymes) |
| [ ] | **ADR** | si `decisions.md` devient long : une fiche `docs/adr/NNNN-titre.md` par décision lourde |
| [ ] | **Runbook** | crash, restore backup, « ça ne démarre plus » — 1 page |
| [ ] | **Journal d’incidents** | `docs/historique/incidents/` — comme les sessions, pour la prod |
| [ ] | **Compatibilité** | tableau versions runtime / OS / navigateurs / loaders |
| [ ] | **Dépréciation** | comment tu tues une API/commande (durée, message, version) |
| [ ] | **Feature flags** | noms, défaut, où c’est lu — évite les `if (true)` oubliés |

### 8.4 Risques selon le type

| Cocher | Extension | Quand c’est utile |
|--------|-----------|-------------------|
| [ ] | **Inventaire RGPD / PII** | dès qu’il y a un email, un pseudo, une IP loggée |
| [ ] | **Threat model 1 page** | auth, argent, données santé, contrôle d’un appareil |
| [ ] | **Licences des deps** | script `license-check` ; interdit GPL dans un binaire proprio si c’est le cas |
| [ ] | **Politique d’assets** | qui possède les images/modèles ; pas d’asset copyrighté « trouvé sur Google » |
| [ ] | **Accessibilité** | toute UI (web, overlay jeu, app) |
| [ ] | **Budget perfs** | « premier écran < Xs », taille max artefact |
| [ ] | **Backup / restore** | sauvegardes utilisateur, saves, bases — test de restore **une** fois |
| [ ] | **Signing / stores** | Android, iOS, mods (Modrinth) : credentials hors git, checklist publish |
| [ ] | **Schéma de données** | migrations versionnées ; jamais « ALTER à la main en prod » sans note |
| [ ] | **Pinout / BOM** | hardware : versions de carte, fournisseurs, firmware compatible |

### 8.5 Cursor / agent (très rentable)

| Cocher | Extension | Pourquoi |
|--------|-----------|----------|
| [ ] | **Skill métier** | 1 skill pour le truc **récurrent et technique** (pas une rule de 200 lignes) |
| [ ] | **User rules vs project rules** | global : langue, git, commits sur demande. Projet : version, domaine |
| [ ] | **Liste MCP** | quels serveurs sont attendus ; doc d’install **sans** tokens |
| [ ] | **Interdit agent** | fichier `docs/AGENT_DONT.md` : pas de push, pas de `--force`, pas de secrets, pas d’amend |
| [ ] | **Prompt de boot** | 5 lignes à coller au 1er chat : « lis le kit, lis la BDD, ne code pas encore » |

### 8.6 Idées « plus tard » (ne pas tout activer jour 0)

- Status / health page, changelog utilisateur **séparé** du changelog technique.
- Telemetry opt-in (jamais silencieuse).
- Programme de beta / feature freeze avant `X.Y.0`.
- Miroir docs (site) généré depuis `docs/` — seulement si tu publies vraiment.
- Politique de support (quelles versions tu corriges).
- `CODE_OF_CONDUCT` si le repo devient public avec contributeurs.
- Playbooks « agent cloud » vs « agent local » (secrets, taille du repo).

---

## 9. Definition of Done (modèle)

À coller dans `docs/DEFINITION_OF_DONE.md` si l’extension est cochée. Adapter `audience`.

Une livraison n’est **finie** que si :

- [ ] Comportement constaté (pas seulement « le code compile »)
- [ ] Version incrémentée dans `fichier_version` uniquement
- [ ] 1–3 bullets WIP (effet pour l’audience)
- [ ] Entrée session (+ index)
- [ ] BDD à jour si un fait réutilisable a changé
- [ ] `CAPABILITIES.md` si le produit a gagné/perdu un comportement
- [ ] Aucun secret, chemin perso, transcript
- [ ] Tests / verif du profil (navigateur, firmware flash, CLI `--help`, …)

**Hors DoD :** refactor cosmétique, logs debug temporaires, bump « pour plus tard ».

---

## 10. Checklist jour 0 (humain + agent)

1. [x] Paramètres §2 remplis, hors-scope rédigé, 1 profil coché
2. [x] Extensions §8 cochées (rester maigre : socle + « fortement recommandé »)
3. [x] Arbre §3 créé ; rules §4 collées et **paramétrées** (plus de `« »`)
4. [ ] `git config user.name` / `user.email` **locaux** = auteur
5. [x] README : quoi, pour qui, comment lancer, où est la version
6. [x] Premier `CHANGELOG_WIP` vide, version initiale écrite
7. [x] `.gitignore` + `.env.example` + `.cursorignore` si choisi
8. [x] Dire à l’agent la **première** conversation slug pour `prompts/`
9. [ ] Commit initial **si demandé**

---

## 11. Ce que l’agent doit / ne doit pas faire

**Doit**

- Lire ce kit + la BDD avant de chercher **en tour d’implémentation**.
- Remplacer tous les `« »` restants ou demander la valeur manquante **une** fois.
- Créer des fichiers vides structurés plutôt que d’inventer du métier.
- Répondre en `langue`.

**Ne doit pas**

- Copier un autre projet « parce que c’est le même auteur ».
- Ajouter Minecraft / un framework / une CI complète sans case cochée.
- Commit, push, tag, publish sans demande.
- Écrire des secrets, des vrais tokens, des chemins personnels.
- Bumper une majeure, sauter des patches, hardcoder la version dans 3 fichiers.

---

## 12. Mini-guide « appliquer le kit » (pour l’agent)

Prompt type, une fois le §2 remplis :

> Applique `docs/kit-nouveau-projet.md` : crée l’arbre manquant, les rules, les squelettes docs. Ne touche pas au code métier. Ne commit pas.

Ordre : paramètres → arbre → rules → VERSIONING / WIP / CAPABILITIES / BDD / prompts INDEX → README → DoD et extensions cochées → récap « fichiers créés + valeurs encore vides ».

---

## 13. PersonnalWorld — ce qu’on a volontairement généralisé

| Ici (générique) | Équivalent PW (ne pas recopier tel quel) |
|-----------------|------------------------------------------|
| `fichier_version` | `gradle.properties` → `mod.version` |
| `CAPABILITIES.md` | capacités **en jeu** |
| Rapport `api-<nom>` | `docs/historique/api-darchitect/` |
| Skill métier | `.cursor/skills/minecraft-geckolib/` |
| Archive `builds/` | jars + PATCHNOTES par version |

Si le **nouveau** projet est encore un mod MC : cocher le module Minecraft **et** garder ce kit comme couche process.

---

## 14. Maintenance de ce kit

Quand une règle process se révèle utile sur **2 projets** : la remonter ici (pas seulement dans un repo).  
Quand une règle n’est utile **qu’à un domaine** : module §7, pas le socle.
