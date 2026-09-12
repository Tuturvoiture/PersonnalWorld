# Conversation : kit-nouveau-projet

- **Slug :** kit-nouveau-projet
- **Créée :** 2026-08-22

## Mémo local

- Kit process Cursor générique (hors Minecraft), copiable vers d’autres dépôts.

## Prompts

### P001 — 2026-08-22

**Demande :** Fichier markdown pour exporter / paramétrer tous les nouveaux projets (pas forcément Minecraft) + propositions d’ajouts.

**Livré :** `docs/kit-nouveau-projet.md` — fiche paramètres YAML, arbre socle, rules génériques, profils, modules opt-in, catalogue d’extensions, DoD, checklist jour 0.

**Pointeurs :** `docs/kit-nouveau-projet.md`

**Suite :** appliquer le kit dans un vrai nouveau repo ; cocher extensions voulues.

### P002 — 2026-08-22

**Demande :** Adapter PersonnalWorld au kit modifié (`Downloads/kit-nouveau-projet.md`) : rules moins always, lecture BDD conditionnelle, extensions 8.1.

**Livré :** Kit PW rempli ; `AGENTS.md` ; always = git-attribution / versioning / prompt-bdd ; WIP + release-cut agent-requested ; sessions + DArchitect en globs ; HORS_SCOPE, DoD, COMMANDS, ENVIRONMENTS, SECURITY, `.env.example`, `.cursorignore`.

**Pointeurs :** `docs/kit-nouveau-projet.md`, `AGENTS.md`, `.cursor/rules/`, `docs/HORS_SCOPE.md`, `docs/COMMANDS.md`

**Suite :** cocher 8.2–8.5 si besoin ; commit sur demande.
