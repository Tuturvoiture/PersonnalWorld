# Sécurité

- Aucun secret runtime pour le gameplay. Ne pas committer `.env`, tokens, keystores, `JAVA_HOME` réel.
- Contrat : [`.env.example`](.env.example). Valeurs réelles hors git.
- Jars `libs/darchitect-*.jar` = API tierce, pas des credentials.
- Docs / rapports / sessions : pas de chemins personnels, pas de tokens, pas de transcripts.
- Si fuite (token collé dans un chat ou un commit) : révoquer, ne pas `amend` sans demande, traiter en nouveau commit.
- Publish Modrinth / CurseForge : credentials hors git ; pas d’upload auto depuis l’agent.
