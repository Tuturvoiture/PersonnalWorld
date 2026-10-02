# PersonnalWorld 1.4.0 (Fabric)

**Minecraft 1.21.1 · Fabric only**

Stable release of the **1.4** line. Changes since **1.3.1**.

## Highlights

- Adventure journal: manage your islands, invites, and roles from a parchment book
- Several personal islands per player (cap configurable; default 3)
- Safe visitors: no break/place, no chests, no hurting entities
- Invite / kick / visit work with the host **online or offline**
- Unfinished island types (Forest, Rock, Desert) stay locked with a **Soon** badge — Classic only for creation
- DimensionArchitect **0.1.2 or newer** is required

## What's new since 1.3.1

### Adventure journal

- New item. Right-click opens the 3D book (GeckoLib optional); closing the screen closes it.
- Tabs: **My islands** and **Invited islands**, three islands per page.
- Create an island: **Classic** only for now. Forest / Rock / Desert are visible but locked (**Soon**).
- Settings (owner): rename, move the spawn cube, terrain / fire / PvP. Weather is not available yet.
- Invitations page: list, roles, kick; add online or offline players by name.
- Co-creators can manage invites on the island they are viewing.
- Join from the book only if you are on the whitelist and the island already exists.

### Several islands

- More than one island per player (`maxIslandsPerPlayer`, default 3).
- The staff and `/pw` follow the **active** island.
- Island gamerules are copied from the Overworld on load/reload; terrain and fire overrides win.

### Invites and roles

- `/pw invite|kick|role|list|visit|leave`
- Targets by name or UUID, **online or offline**
- Whitelist files: `<world>/personnalworld/access/` (persistent across restarts)
- `/pw visit` requires an **existing** island and a whitelist invite — it does not create a dimension
- Temporary (RAM-only) guests are dropped on leave, disconnect, dimension change, or server restart; whitelist members are kept
- Roles sync to DimensionArchitect when it is ready

Roles: **Owner**, **Co-creator**, **Builder**, **Visitor**, plus short-lived **Temp** (RAM).

### Visitor rules

- Visitors get JOIN so they can enter.
- They cannot break or place blocks, open chests / barrels / shulkers / ender chests, or hurt entities.
- On someone else’s island, the staff returns you home without saving that island as a return point.

### Polish (late betas)

- Preset row: four types on one evenly spaced line; locked types show a diagonal **Soon** badge
- If a preset’s structure NBT is missing, generation falls back to **ile_1**
- Book close animation: no forced snap, no open flash, no hotbar “re-select” hop
- Visiting an offline host reloads a persisted island if it was unloaded (no blank world created)

### DimensionArchitect quota

- On server start, PersonnalWorld sets DimensionArchitect `[dimensions].max_simultaneous` to **64** (disable with `syncDarchitectMaxSimultaneous = false`).

## Requirements

- Minecraft **1.21.1**
- Fabric Loader **≥ 0.18.4**
- Fabric API **≥ 0.116.0** (recommended `0.116.0+1.21.1`)
- Architectury API **≥ 13.0.8**
- **DimensionArchitect 0.1.2 or newer** (required)

### Optional

- GeckoLib **4.7.5.1** (animated staff and adventure book)

## Not in this release

- Weather control
- Ban / BANNED behaviour (enum only)
- Unlocking Forest / Rock / Desert (structures not ready)
- NeoForge build (still deferred)
- Full public unload/load API for `reload-island` (needs DimensionArchitect ≥ 0.1.3)

## Notes for server owners

- Update DimensionArchitect to **0.1.2+** before this jar, then restart once.
- Expect `max_simultaneous` to become **64** unless you set `syncDarchitectMaxSimultaneous = false`.
- Debug commands (`/pw debug …`) stay off unless `enableDebugCommands` is true.

---

Jar: `1.4.0-fabric.jar`
