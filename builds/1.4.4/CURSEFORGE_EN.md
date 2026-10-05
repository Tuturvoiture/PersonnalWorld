# PersonnalWorld 1.4.4 (Fabric)

**Minecraft 1.21.1 · Fabric only**

Patch release on the **1.4** line. Changes since **1.4.0**.

## Highlights

- Switching the **active** island unloads your other islands (with a 2-minute cooldown)
- Guests can only visit an island that is **already loaded** — owners must activate it first
- Clearer island messages; spawn button renamed; book button tooltips (EN/FR)
- “Reserve” keep-loaded is shown as **Soon** (not implemented yet)

## What's new since 1.4.0

### Active island & loading

- Making an island active loads it and **unloads** your other personal islands (players there are sent home first)
- Cooldown between active switches: **2 minutes** (`activeIslandSwitchCooldownSeconds` in `personnalworld.toml`)
- `/pw visit` / Join refuse unloaded islands with a message asking the owner to activate them
- Spawning or staying on an inactive / unauthorized island sends you back to your **saved return** position

### Spawn & book UX

- Button label: **Change spawn** / **Changer le spawn**
- After confirm: chat says the spawn is now under your feet (facing is saved silently at 90°)
- Hover tooltips on book buttons (French and English)
- Clearer messages when an island is deactivated (not “reloaded”)
- **Reserve** button visible as **Soon** (extra keep-loaded islands — still TODO)

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
- Unlocking Forest / Rock / Desert
- Keep-loaded **Reserve** (UI placeholder only)
- NeoForge build (still deferred)
- Full public unload/load API (needs DimensionArchitect ≥ 0.1.3)

## Notes for server owners

- DimensionArchitect **0.1.2+** still required.
- Active-island unload uses the existing DA unload path; guests on a non-active island are sent home first.
- Debug commands (`/pw debug …`) stay off unless `enableDebugCommands` is true.

---

Jar: `1.4.4-fabric.jar`
