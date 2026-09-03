# Conversation : reprise-contexte-regles

- **Slug :** reprise-contexte-regles
- **Créée :** 2026-08-22

## Mémo local

- Recap projet + recap rules déjà faits dans ce chat.
- BDD agent créée ce tour (`docs/historique/bdd/`, `docs/historique/prompts/`).

## Prompts

### P001 — 2026-08-22

**Demande :** Reprendre l’intégralité du projet et des anciennes conversations.

**Livré :** Recap architecture (surcouche DArchitect), version alpha.14, fil DimLib→API / île / GeckoLib, ouvert (vérif bâton, NeoForge, docs, commit).

**Pointeurs :** `docs/historique/sessions/INDEX.md`, `docs/CHANGELOG_WIP.md`, `docs/CAPABILITIES.md`

**BDD :** repris dans `bdd/etat-courant.md` + `bdd/decisions.md`

### P002 — 2026-08-22

**Demande :** Recap des Cursor rules.

**Livré :** 7 rules : versioning, changelog-wip, sessions, release-cut, git-attribution, darchitect-api-reports (always), geckolib (globs).

**Pointeurs :** `.cursor/rules/*.mdc`

### P003 — 2026-08-22

**Demande :** Recap des Cursor rules (suite / même sujet).

**Livré :** même recap (tour précédent).

**Pointeurs :** `.cursor/rules/`

### P004 — 2026-08-22

**Demande :** Nouvelle règle : fichier prompt incrémenté par nom de conversation + BDD agent pour éviter recherches / relectures inutiles.

**Livré :** règle `prompt-bdd.mdc` (alwaysApply) ; dossiers `docs/historique/prompts/` et `docs/historique/bdd/` ; seed état / fichiers / décisions ; ce journal.

**Pointeurs :** `.cursor/rules/prompt-bdd.mdc`, `docs/historique/bdd/INDEX.md`, `docs/historique/prompts/INDEX.md`
