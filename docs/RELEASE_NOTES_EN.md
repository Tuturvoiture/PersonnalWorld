## PersonnalWorld 1.3.0-beta.0

**Beta build for player feedback.** Please report bugs, crashes, and gameplay issues (staff, teleport, personal world, spawn).

### Highlights

- Each player can open a personal void world with the personal-world staff.
- Custom 3D staff model.
- Optional GeckoLib animations (idle + short pulse on right-click). Without GeckoLib, the staff stays static and the mod still works.
- Shared inventory between the Overworld and your personal world.
- Reliable teleport using an unbreakable spawn marker.

### Added

- Personal dimension per player, created on first staff use.
- Island structure in the void (spawn around 0, 90, 0).
- Unbreakable spawn marker used as the teleport reference (feet at Y=89).
- `/returnworld` command to return to your saved origin position.

### Changed

- Personal worlds are created via DimensionArchitect (DimLib is no longer required).
- Inventory is shared across dimensions when a personal world is created.
- Staff hand and GUI poses tuned for both the static model and the animated path.

### Fixed

- Safer personal-world teleport: prefer the spawn marker, then a ground scan under spawn, then a bedrock fallback.
- Spawn marker texture and animation frames display correctly.
- Staff placement stays correct when GeckoLib is installed.
- Without GeckoLib, no crash — static 3D staff only.

### Requirements

- Minecraft 1.21.1
- Fabric
- Fabric API
- Architectury API
- DimensionArchitect 0.0.59 or newer

### Optional

- GeckoLib 4.7.5.1 (recommended for this version)

### Breaking change (from 1.2.x)

- Old DimLib personal worlds are not migrated automatically.
- Use a new world (or recreate your personal dimension) and copy builds if needed.
