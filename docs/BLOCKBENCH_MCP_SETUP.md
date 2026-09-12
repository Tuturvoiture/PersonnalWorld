# Setup Blockbench MCP (animations Minecraft)

Aucun MCP Minecraft n’est branché par défaut dans cette session. Pour que l’agent pilote **Blockbench** (modèles, textures, keyframes GeckoLib) en direct :

## Prérequis

1. **Blockbench desktop** ≥ 4.8 (pas la version web).
2. Plugin **GeckoLib** dans Blockbench (File → Plugins).
3. **Node.js 18+** installé et dans le `PATH` (`node -v` doit marcher).
4. Redémarrer le terminal / Cursor après install Node.

## Plugin installé : jasonjgardner/blockbench-mcp-plugin

Le plugin héberge le MCP **dans Blockbench** (pas de serveur Node séparé).

- URL par défaut : `http://127.0.0.1:3000/bb-mcp`
- Réglages : Settings → General → **MCP Server Port** / **MCP Server Endpoint**
- Config Cursor (ce repo) : `.cursor/mcp.json`
- Vérif : Blockbench ouvert + serveur MCP démarré

```bat
curl -s -X POST http://127.0.0.1:3000/bb-mcp -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" -d "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{\"protocolVersion\":\"2025-03-26\",\"capabilities\":{},\"clientInfo\":{\"name\":\"test\",\"version\":\"1\"}}}"
```

Après modification de `mcp.json` : **recharger les MCP** ou redémarrer Cursor.

## Option alternative : sosadly/blockbench-mcp


Repo : https://github.com/sosadly/blockbench-mcp

### 1. Plugin dans Blockbench

1. Cloner ou télécharger le repo.
2. File → Plugins → **Load Plugin from File** → `plugin/blockbench_mcp.js`.
3. Autoriser le réseau (**Always allow**).
4. Toast : MCP sur le port **8787**. Vérifier : Tools → Start MCP Server.

```bat
curl http://127.0.0.1:8787/ping
```

### 2. Build du serveur MCP

```bat
git clone https://github.com/sosadly/blockbench-mcp.git D:\OneDrive\Documents\Cursor\tools\blockbench-mcp
cd /d D:\OneDrive\Documents\Cursor\tools\blockbench-mcp
npm install
npm run build
```

### 3. Config Cursor

Ajouter dans `.cursor/mcp.json` (utilisateur) ou créer `.cursor/mcp.json` **dans ce repo** :

```json
{
  "mcpServers": {
    "blockbench": {
      "command": "node",
      "args": [
        "D:/OneDrive/Documents/Cursor/tools/blockbench-mcp/dist/index.js"
      ],
      "env": {
        "BLOCKBENCH_MCP_PORT": "8787"
      }
    }
  }
}
```

Puis **recharger les MCP** / redémarrer Cursor. Blockbench doit rester ouvert avec le serveur MCP démarré.

## Alternatives

| Projet | Notes |
|--------|--------|
| [SwagRee/BlockBenchMCP](https://github.com/SwagRee/BlockBenchMCP) | Plugin-only (HTTP), format GeckoLib P0 |
| [@adhisang/minecraft-blockbench-mcp](https://www.npmjs.com/package/@adhisang/minecraft-blockbench-mcp) | Outils `geckolib_*` dédiés |
| [jasonjgardner/blockbench-mcp-project skills](https://github.com/jasonjgardner/blockbench-mcp-project/tree/main/skills) | Skills `blockbench-animation`, etc. |

## Skill locale (déjà dans ce repo)

`.cursor/skills/minecraft-geckolib/SKILL.md` — chemins PersonnalWorld, soft-dep, workflow export sans MCP.

## État actuel (PersonnalWorld)

- Anims provisoires : `idle` / `use` dans `animations/item/personnal_world_item.animation.json`.
- À remplacer plus tard par un export Blockbench Animate.
