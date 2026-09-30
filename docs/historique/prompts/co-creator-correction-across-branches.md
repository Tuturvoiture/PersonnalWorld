# Co-creator correction across branches

## P001

**Demande :** Retirer le co-créateur Cursor de toutes les branches ; ne garder que l’auteur humain.

**Livré :** Trailers `Co-authored-by: Cursor` retirés sur `feature/multi-version-multi-loader` (`git filter-branch` plage `1d1520d..HEAD`) ; force-push `origin/feature/multi-version-multi-loader` (`fd65ab9`). Autres branches sans trailer Cursor.

**Pointeurs :** `git log --all --grep='Co-authored-by: Cursor'` → vide ; auteurs = `GalsaxX_FR` seul.

**Suite :** Optionnel — retirer aussi `Co-authored-by: GalsaxX` (toi) sur 2 commits scaffold ; nettoyer fichiers junk non trackés (`body*.txt`, `headers*.txt`, `debug-*.log`).
