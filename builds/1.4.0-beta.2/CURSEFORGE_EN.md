# PersonnalWorld 1.4.0-beta.2 (Fabric)

**Minecraft 1.21.1 · Fabric only**

Changes since **1.3.1**.

## Highlights

- Adventure journal: list your islands, create more, edit settings, and manage invites
- Invite friends from the book or with `/pw` (online or offline)
- Visitors can visit safely: no break/place, no chests, no hurting entities; builders and co-creators keep their rights
- Move the spawn cube, rename the island, and toggle terrain, fire, and PvP
- DimensionArchitect **0.1.2 or newer** is required

## What's new

### Adventure journal

- New item. Right-click opens the 3D book; closing the screen closes it. Only **your** book animates when you open it.
- Two tabs: **My islands** and **Invited islands**, three islands per page.
- Invited islands show the island name, the **owner’s player name**, and your role.
- Create an island: Classic, Forest, or Rock, then a name.
- Settings (owner): rename, move the spawn cube, terrain / fire / PvP. Weather is not available yet.
- Moving the spawn cube only works while you stand on that island, on a solid full block. The block you replace is put back on the next move.
- **Invitations** page: list of people already invited with their role and kick. **Add** opens connected players; click a name to fill the field, then Refresh or Add. You can also type a name (offline). New invites default to Visitor; change the role on the list afterward.
- Role changes in the book apply when you leave Invitations or close the book: one chat line for the last change only.
- Co-creators can manage invites on the island they are viewing.
- Join from the book only if you are on the whitelist and the island already exists.
- Readable parchment UI (white text with outline) in all ten mod languages.

### Several islands

- More than one island per player. Cap: `maxIslandsPerPlayer` in `config/personnalworld.toml` (default 3).
- The staff and `/pw` follow the **active** island, not only the first one.
- The first world created with the staff is named **World 1** (or **Monde 1** in French).
- Island gamerules are copied from the Overworld on load and reload. Terrain and fire overrides win. Island PvP off cancels player-vs-player damage on that island.

### Invites and roles

- `/pw invite <player> <co_creator|builder|visitor>`
- `/pw kick <player>`
- `/pw role <player> <role>`
- `/pw list`
- `/pw visit <player> [islandName]` — only if invited and the island already exists
- `/pw leave` — owners cannot leave their own island
- Targets work by name or UUID, online or offline.
- Whitelist files: `<world>/personnalworld/access/`
- Roles sync to DimensionArchitect when it is ready.

Roles: **Owner**, **Co-creator**, **Builder**, **Visitor**, plus short-lived **Temp** visitors.

### Visitor rules

- Visitors get JOIN so they can enter the island.
- They cannot break or place blocks, open chests / barrels / shulkers / ender chests, or hurt any entity (players, animals, projectiles, pets, or entities named after them).
- On someone else’s island, the staff returns you home without saving that island as a return point.
- Visiting an island saves your current position like the staff (`noDimensionSavePosition` still applies).

### Messages

- Teleport feedback (staff, return, visit) uses the **action bar**. Toggle with `actionBarMessages` in `personnalworld.toml` (default true).
- Invitation received stays in **chat**.

### DimensionArchitect quota

- On server start, PersonnalWorld sets DimensionArchitect `[dimensions].max_simultaneous` to this mod’s quota (**64**), then reloads DA config.
- Disable with `syncDarchitectMaxSimultaneous = false` in `personnalworld.toml`.

## Fixes

- Client no longer crashes on startup from the member list being registered twice.
- Joining a world no longer crashes when DimensionArchitect is not ready yet.
- Opening the adventure book no longer opens every other player’s book model.

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

## Not in this beta

- Weather control
- Ban / BANNED behaviour (enum only)

## Notes for server owners

- Update DimensionArchitect to **0.1.2+** before this jar, then restart once.
- Expect `max_simultaneous` to become **64** unless you set `syncDarchitectMaxSimultaneous = false`.
- Debug commands (`/pw debug …`) stay off unless `enableDebugCommands` is true in `personnalworld.toml`.

---

Jar: `1.4.0-beta.2-fabric.jar`
