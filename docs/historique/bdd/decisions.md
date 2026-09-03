# Décisions durables

- PW = **surcouche** : création de dim = DArchitect. Bug API → rapport `docs/historique/api-darchitect/`, pas de patch API depuis ce workspace.
- IDs dim : `personnalworld:perso_<uuid>` ; quota DArchitect 64 ; type VOID.
- Breaking 1.3 : **pas** de migrateur auto DimLib ; doc manuelle « monde neuf + copie builds » : `docs/MIGRATION_DIMLIB.md`.
- Île : classpath NBT ; spawn **0 90 0** ; pas de plateforme SKYBLOCK 3×3 (`SpawnResolver`) ; preset SKYBLOCK pour couper les structures vanilla.
- Inventaire mondes perso : **toujours partagé** avec l’Overworld (`DimensionBuilder.isolatePlayerData(false)` à la création ; DArchitect ≥ 0.0.58).
- TP monde perso : marqueur `spawn_marker` (**Y=88**, pieds Y=89) ; scan 50 blocs ; bedrock Y=87 = secours ultime.
- Item / assets / `/returnworld` / mixins gameplay = module **common** (parité NeoForge).
- GeckoLib **optionnel** : sans le mod, modèle JSON statique, pas de crash.
- `mod.version` sans suffixe `+1.21.1` ; `${version}` dans les métadonnées loader.
- Always-apply Cursor : `git-attribution.mdc`, `versioning.mdc`, `prompt-bdd.mdc` (BDD seulement en tour d’implémentation). WIP / sessions / release-cut / DArchitect = glob ou agent-requested.
- Nouveaux dépôts Cursor : copier / remplir `docs/kit-nouveau-projet.md` (process générique). Ne pas recopier Gradle / GeckoLib / DArchitect hors mod MC.
- Commits : auteur `GalsaxX_FR` ; jamais Cursor / Co-authored-by bot dans les messages.
- Majeure `2.0` seulement sur demande explicite.
- Cut release seulement si l’utilisateur dit clairement que c’est en ligne.
