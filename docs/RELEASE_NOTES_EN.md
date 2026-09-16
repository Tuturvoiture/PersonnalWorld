## PersonnalWorld 1.3.1

### Highlights

- First public Fabric release of the 1.3 line (DimensionArchitect personal worlds).
- Server config file `config/personnalworld.toml` (English comments + examples).
- Shared inventory by default; safer return when no saved position exists.

### Added

- Personal dimension per player, created on first staff use.
- Island structure in the void (spawn around 0, 90, 0).
- Unbreakable spawn marker used as the teleport reference (feet at Y=89).
- `/returnworld` command (only from your personal world).
- `config/personnalworld.toml`:
  - `noDimensionSavePosition` — dimensions where return position is **not** saved
  - `noDimensionTeleport` — dimensions where the staff **blocks** travel to the personal world
  - `staffCooldownTicks` — staff cooldown (default 40 = 2 seconds)
  - `shareInventory` — shared inventory with the Overworld on **new** personal worlds (default true; does not change worlds already created)

### Changed

- Personal worlds are created via DimensionArchitect (DimLib is no longer required).
- Return position is saved from any non-personal dimension except those listed in `noDimensionSavePosition` (not only Overworld / Nether / End).
- If no return position is saved, teleport uses the player bed/respawn point, otherwise Overworld spawn.
- Staff hand and GUI poses tuned for both the static model and the animated path.
- Packaging cleanup: removed debug stubs, unused client mixin, obsolete GeckoLib resource pack, and source-only texture junk from the jar.
- Tighter Fabric metadata depends (`fabric-api` ≥ 0.116.0, `architectury` ≥ 13.0.8).

### Fixed

- Island init runs synchronously before teleport (no empty void race).
- Safer personal-world teleport: spawn marker, then ground scan, then bedrock fallback (2-block stand space).
- Null-safe return dimension id; translated error messages.
- Island load prefers `personnalworld:ile_1`, then classpath NBT (no `minecraft:ile_1`).
- Spawn marker texture and animation frames display correctly.
- Without GeckoLib, no crash — static 3D staff only.

### Requirements

- Minecraft 1.21.1
- Fabric Loader ≥ 0.18.4
- Fabric API ≥ 0.116.0 (recommended `0.116.0+1.21.1`)
- Architectury API ≥ 13.0.8
- DimensionArchitect 0.0.59 or newer

### Optional

- GeckoLib 4.7.5.1 (recommended for this version)

### Breaking change (from 1.2.x)

- Old DimLib personal worlds are not migrated automatically.
- Use a new world (or recreate your personal dimension) and copy builds if needed.

---

## Template (next release)

Copy the block above, bump the version heading, and replace sections as needed for Modrinth / CurseForge.
