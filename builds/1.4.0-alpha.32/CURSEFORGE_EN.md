# PersonnalWorld 1.4.0-alpha.32 (Fabric)

**Minecraft 1.21.1 · Fabric only**

Changes since **1.3.1**.

## Highlights

- Adventure journal: list your islands, create more, and edit the one you own
- Invite friends from the book or with `/pw` (online or offline)
- Move the spawn cube, rename the island, and toggle terrain, fire, and PvP
- DimensionArchitect **0.1.2 or newer** is required

## What's new

### Adventure journal

- New item. Right-click opens the 3D book; closing the screen closes it.
- Two tabs: **My islands** and **Invited islands**, three islands per page.
- Create an island: Classic, Forest, or Rock, then a name.
- Settings (owner): rename, move the spawn cube, terrain / fire / PvP. Weather is not available yet.
- Moving the spawn cube only works while you stand on that island, on a solid full block. The block you replace is put back on the next move.
- Invites from the book: invite a player, change their role, or remove them.
- The book opens on your active island. All ten mod languages include the new text.

### Several islands

- More than one island per player. The cap is `maxIslandsPerPlayer` in `config/personnalworld.toml` (default 3).
- The staff and `/pw` follow the **active** island, not only the first one.
- The first world created with the staff is named **World 1** (or **Monde 1** if the client language is French).
- Island gamerules are copied from the Overworld on load and reload. Terrain and fire overrides win. Island PvP off cancels player-vs-player damage on that island.

### Invites and roles

- `/pw invite <player> <co_creator|builder|visitor>`
- `/pw kick <player>`
- `/pw role <player> <role>`
- `/pw list`
- `/pw visit <player> [islandName]` — temporary visit; the host can be offline
- `/pw leave` — owners cannot leave their own island
- Targets work by name or UUID, online or offline.
- Whitelist files: `<world>/personnalworld/access/`
- Roles are sent to DimensionArchitect on the first server tick, once it is ready.

Roles: **Owner**, **Co-creator**, **Builder**, **Visitor**, plus short-lived **Temp** visitors from `/pw visit`.

## Fixes

- The client no longer crashes on startup from the member list being registered twice.
- Joining a world no longer crashes when DimensionArchitect is not initialized yet.

## Also

- New mod icon.

## Requirements

- Minecraft **1.21.1**
- Fabric Loader **≥ 0.18.4**
- Fabric API **≥ 0.116.0** (recommended `0.116.0+1.21.1`)
- Architectury API **≥ 13.0.8**
- **DimensionArchitect 0.1.2 or newer** (required)

### Optional

- GeckoLib **4.7.5.1** (animated staff and adventure book; without it the static models still load)

## Not in this alpha

- Weather control
- Ban / BANNED behaviour (enum only)
- NeoForge build

## Notes for server owners

- Update DimensionArchitect to **0.1.2+** before this jar, then restart once.
- Debug commands (`/pw debug …`) stay off unless `enableDebugCommands` is true in `personnalworld.toml`.

---

Jar: `1.4.0-alpha.32-fabric.jar`
