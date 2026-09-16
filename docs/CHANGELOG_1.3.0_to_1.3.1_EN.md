# PersonnalWorld — Changelog 1.3.0 → 1.3.1

English notes for upgrading from **1.3.0** to **1.3.1** (Fabric, Minecraft 1.21.1).  
Does not replace the full 1.3.1 storefront notes in [`RELEASE_NOTES_EN.md`](RELEASE_NOTES_EN.md).

## Changed

- Public docs updated: README, English release notes, and capabilities (server config, shared inventory, `/returnworld` behaviour).
- Tighter Fabric metadata depends: `fabric-api` ≥ 0.116.0, `architectury` ≥ 13.0.8.
- `fabric.mod.json` contact links cleaned up (Modrinth homepage, GitHub sources/issues).

## Fixed / packaging

- Removed the obsolete embedded GeckoLib resource pack (could break the staff model if enabled without GeckoLib).
- Removed debug stubs from the jar (`TestCopieNBT`, empty example client mixin).
- Removed source-only / junk files from the jar (`.pdn`, `.backup`, `test.nbt`, unused `_3d` item model).
- Docs corrected: `/returnworld` outside a personal world is **rejected** with a message (it does not teleport).

## Note

- **1.3.0** was not published on Modrinth/CurseForge. **1.3.1** is the first public storefront build of the 1.3 line.
- Gameplay features introduced during the 1.3.0 cycle (e.g. `config/personnalworld.toml`, safer returns) are already in the 1.3.1 jar; this file only lists what changed **between** 1.3.0 and 1.3.1.
