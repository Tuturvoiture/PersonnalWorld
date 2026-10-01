# PersonnalWorld 1.4.0-beta.7 (Fabric)

**Minecraft 1.21.1 · Fabric only**

Changes since **1.4.0-beta.2**.

## Highlights

- Unfinished island types (Forest, Rock, Desert…) are locked in the journal with a diagonal **Soon** badge
- Preset row layout: all four types on one evenly spaced line
- Book close animation fixed (no forced snap, no open flash, no hotbar “re-select” hop)
- If a preset’s structure NBT is missing, generation falls back to **ile_1**

## What's new / changed

### Locked island presets (beta.3–beta.5)

- Only **Classic** is selectable for creation.
- **Forest**, **Rock**, and **Desert** stay visible but are not clickable.
- Locked icons show a diagonal **Soon** / **Bientôt** overlay on the picture (not in the type name).
- The four presets sit on a **single row**, evenly spaced with equal side margins (Desert no longer wraps under the name field).
- Labels under icons are no longer clipped by a neighboring icon.

### Generation fallback (beta.3)

- If the NBT for the chosen type is missing, the island still generates using **ile_1**.

### Adventure book close animation (beta.6–beta.7)

- Closing the journal plays the **close** animation again (it was being forced shut too early).
- After you have opened the book once, closing no longer flashes open for half a second at the end.
- Closing no longer nudges the item like changing hotbar slots (open/close state is no longer written to stack NBT at the end of the anim).

## Requirements

Unchanged from beta.2:

- Minecraft **1.21.1**
- Fabric Loader **≥ 0.18.4**
- Fabric API **≥ 0.116.0** (recommended `0.116.0+1.21.1`)
- Architectury API **≥ 13.0.8**
- **DimensionArchitect 0.1.2 or newer** (required)

### Optional

- GeckoLib **4.7.5.1** (animated staff and adventure book)

## Not in this beta

- Weather control
- Ban / BANNED behaviour (enum only)
- Unlocking Forest / Rock / Desert (structures not ready yet)

## Version path

| Version | Note |
|---------|------|
| 1.4.0-beta.3 | Lock unfinished presets; NBT fallback to `ile_1` |
| 1.4.0-beta.4 | Diagonal Soon badge; more spacing between presets |
| 1.4.0-beta.5 | Four presets on one evenly spaced row |
| 1.4.0-beta.6 | Close animation visible again |
| 1.4.0-beta.7 | No open flash / re-select hop at end of close |

---

Jar: `1.4.0-beta.7-fabric.jar`
