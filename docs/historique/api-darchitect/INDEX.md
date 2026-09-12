# Rapports API DimensionArchitect

Fiches **copiables** dans le dépôt sœur `DimensionArchitectAPI` (Cursor API).  
PersonnalWorld ne corrige pas l’API ici — voir règle `.cursor/rules/darchitect-api-reports.mdc`.

| Date | API | Sévérité | Titre | Statut PW | Fichier |
|------|-----|----------|-------|-----------|---------|
| 2026-08-30 | 0.0.59 | — | PATCHNOTES build API (copie locale) | jars `libs/` ≥ 0.0.59 ; inventaire partagé PW | [PATCHNOTES-0.0.59.txt](PATCHNOTES-0.0.59.txt) |
| 2026-08-30 | 0.0.57 | — | PATCHNOTES build API (copie locale) | jars `libs/` ≥ 0.0.57 | [PATCHNOTES-0.0.57.txt](PATCHNOTES-0.0.57.txt) |
| 2026-08-18 | 0.0.44 | P3 | Pas de flag builder skip plateforme SKYBLOCK | contourné (`SpawnResolver`) | [2026-08-18_skip-starter-platform.md](2026-08-18_skip-starter-platform.md) |
| 2026-08-18 | 0.0.44 | P2 | VOID garde les structures vanilla (`Optional.empty()`) | contourné (preset SKYBLOCK) | [2026-08-18_void-structures.md](2026-08-18_void-structures.md) |
| 2026-08-17 | 0.0.43 | P0 | Nested jars Tomlj ignorés par Loom `files()` | **corrigé** dans 0.0.44 (shadow+relocate) | [2026-08-17_tomlj-loom-nested-jars.md](2026-08-17_tomlj-loom-nested-jars.md) |
| 2026-08-17 | 0.0.42 | P0 | Jar runtime sans Tomlj → crash init | **corrigé** dans 0.0.43 (prod) | [2026-08-17_tomlj-runtime-jar.md](2026-08-17_tomlj-runtime-jar.md) |
