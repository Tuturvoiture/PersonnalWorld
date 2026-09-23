# PersonnalWorld 1.4.0-beta.0 (Fabric)

**Minecraft 1.21.1 · Fabric only**

First public **beta** of the 1.4 line: invite friends to your personal island and manage roles.

## Highlights

- Invite / kick / roles on personal islands (`/pw`)
- Works with players **online or offline** (name or UUID)
- DimensionArchitect **0.1.2+** enforces access; PersonnalWorld keeps the whitelist as source of truth
- Temporary visits without a permanent invite

## What's new

### Island invites & permissions

- `/pw invite <player> <co_creator|builder|visitor>` — add someone to your island whitelist
- `/pw kick <player>` — remove whitelist / temp access (and eject them if they are online)
- `/pw role <player> <role>` — change an existing member’s role
- `/pw list` — list members and temporary visitors
- `/pw visit <player> [islandName]` — visit another player’s active island (host can be offline)
- `/pw leave` — leave the island you are currently in (owners cannot leave their own)

Roles: **Owner**, **Co-creator**, **Builder**, **Visitor**, plus short-lived **Temp** visitors from `/pw visit`.

Whitelist files live under `<world>/personnalworld/access/` (auto-saved, revisioned, corrupt files quarantined).

### Access sync (DimensionArchitect ≥ 0.1.2)

- Islands use **MANAGED** access with a logical owner
- Role updates are applied atomically to DimensionArchitect (no leftover build rights after a kick)
- Soft migration for older open-access personal dims

### Also included from the 1.3 line (if you skipped those builds)

- Server config: `config/personnalworld.toml`
- Shared inventory by default (`shareInventory`)
- Safer return teleport and spawn marker

## Commands (quick)

| Command | Who | Effect |
|---------|-----|--------|
| `/pw invite …` | Owner / co-creator | Permanent invite |
| `/pw kick …` | Owner / co-creator | Remove access |
| `/pw role …` | Owner / co-creator | Change role |
| `/pw list` | Player | Member list |
| `/pw visit …` | Player | Visit island (temp if needed) |
| `/pw leave` | Visitor / member | Leave current island |
| `/returnworld` | In personal world | Return home |

## Requirements

- Minecraft **1.21.1**
- Fabric Loader **≥ 0.18.4**
- Fabric API **≥ 0.116.0** (recommended `0.116.0+1.21.1`)
- Architectury API **≥ 13.0.8**
- **DimensionArchitect 0.1.2 or newer** (required)

### Optional

- GeckoLib **4.7.5.1** (animated staff; without it you still get the static 3D staff)

## Not in this beta

- Adventure-book / GUI for invites (API ready; UI later)
- Ban system (enum only for now)
- NeoForge build (still deferred)

## Notes for server owners

- After updating DimensionArchitect to **0.1.2+**, restart once so access modes migrate cleanly.
- Debug commands (`/pw debug …`) stay off unless you enable `enableDebugCommands` in `personnalworld.toml`.

---

Jar: `personnalworld-fabric-1.4.0-beta.0+1.21.1.jar`
