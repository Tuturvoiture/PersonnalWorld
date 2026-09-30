package fr.galsaxx.island;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IslandLogicTest {
	private static final UUID OWNER = UUID.fromString("11111111-2222-3333-4444-555555555555");

	@Test
	void legacyIdKeepsUuidAndSlotZero() {
		String id = IslandIdParser.dimensionId(OWNER, 0);
		assertEquals(OWNER, IslandIdParser.creatorUuid(id).orElseThrow());
		assertEquals(0, IslandIdParser.slotIndex(id));
	}

	@Test
	void indexedIdKeepsUuidAndSlot() {
		String id = IslandIdParser.dimensionId(OWNER, 2);
		assertEquals(OWNER, IslandIdParser.creatorUuid(id).orElseThrow());
		assertEquals(2, IslandIdParser.slotIndex(id));
		assertTrue(id.endsWith("_2"));
	}

	@Test
	void invalidIdIsEmpty() {
		assertTrue(IslandIdParser.creatorUuid("personnalworld:perso_not-a-uuid").isEmpty());
		assertEquals(-1, IslandIdParser.slotIndex("minecraft:overworld"));
		assertEquals(-1, IslandIdParser.slotIndex("personnalworld:perso_short"));
	}

	@Test
	void pageCountAndLabel() {
		assertEquals(1, IslandPageLayout.pageCount(0));
		assertEquals(1, IslandPageLayout.pageCount(3));
		assertEquals(2, IslandPageLayout.pageCount(4));
		assertEquals("1/1", IslandPageLayout.label(1, IslandPageLayout.pageCount(0)));
		assertEquals("2/5", IslandPageLayout.label(2, IslandPageLayout.pageCount(13)));
	}

	@Test
	void navigationBlockedOnSinglePageAndOutOfRange() {
		assertEquals(1, IslandPageLayout.tryPrevious(1, 1));
		assertEquals(1, IslandPageLayout.tryNext(1, 1));
		assertEquals(1, IslandPageLayout.tryJump(1, 1, 3));
		assertEquals(2, IslandPageLayout.tryJump(2, 5, 0));
		assertEquals(2, IslandPageLayout.tryJump(2, 5, 99));
		assertEquals(4, IslandPageLayout.tryJump(2, 5, 4));
	}

	@Test
	void oddPageIsVEvenPageIsLambda() {
		IslandPageLayout.Anchors odd = IslandPageLayout.anchors(1);
		IslandPageLayout.Anchors even = IslandPageLayout.anchors(2);
		assertTrue(odd.centerBias() > odd.leftBias());
		assertTrue(even.centerBias() < even.leftBias());
		assertEquals(2, IslandPageLayout.pageOfIsland(3));
	}

	@Test
	void creationPolicy() {
		assertFalse(IslandCreationPolicy.evaluate(3, 3).allowed());
		assertEquals(IslandCreationPolicy.Reason.MAX, IslandCreationPolicy.evaluate(3, 3).reason());
		assertTrue(IslandCreationPolicy.evaluate(2, 3).allowed());
		assertTrue(IslandCreationPolicy.evaluate(10, 0).allowed());
	}

	@Test
	void presetsMergeAndFallback() {
		var extra = List.of(new IslandPresetRegistry.IslandPreset("custom", "Custom", ""));
		var merged = IslandPresetRegistry.merge(IslandPresetRegistry.defaults(), extra);
		assertEquals(4, merged.size());
		assertEquals(IslandPresetRegistry.FALLBACK_ICON, IslandPresetRegistry.find(merged, "custom").orElseThrow().icon());
		assertTrue(IslandPresetRegistry.find(merged, "missing").isEmpty());
	}

	@Test
	void gamerulesCopyUnlessOverlay() {
		Map<String, String> overworld = Map.of("keepInventory", "true", "pvp", "true");
		Map<String, String> dim = Map.of("keepInventory", "false", "pvp", "true");
		Map<String, String> copied = IslandGameruleResolver.resolve(true, overworld, dim, Map.of());
		assertEquals("true", copied.get("keepInventory"));

		Map<String, String> overlay = Map.of("pvp", "false");
		Map<String, String> withOverlay = IslandGameruleResolver.resolve(true, overworld, Map.of("pvp", "true"), overlay);
		assertEquals("false", withOverlay.get("pvp"));

		Map<String, String> noSync = IslandGameruleResolver.resolve(false, overworld, dim, overlay);
		assertEquals("false", noSync.get("keepInventory"));
		assertEquals("false", noSync.get("pvp"));
	}

	@Test
	void defaultNameFollowsLanguageAndSlot() {
		assertEquals("Monde 1", IslandDefaultName.of("fr_fr", 0));
		assertEquals("Monde 3", IslandDefaultName.of("fr_ca", 2));
		assertEquals("World 1", IslandDefaultName.of("en_us", 0));
		assertEquals("World 2", IslandDefaultName.of(null, 1));
	}

	@Test
	void spawnMoveRejectsAirBarrierChestAndWrongDimension() {
		assertFalse(SpawnMove.plan(false, true, SpawnMove.FloorKind.SOLID, false, "", "minecraft:stone", MARKER).ok());
		assertFalse(SpawnMove.plan(true, false, SpawnMove.FloorKind.SOLID, false, "", "minecraft:stone", MARKER).ok());
		assertFalse(SpawnMove.plan(true, true, SpawnMove.FloorKind.AIR, false, "", "minecraft:air", MARKER).ok());
		assertFalse(SpawnMove.plan(true, true, SpawnMove.FloorKind.FLUID, false, "", "minecraft:water", MARKER).ok());
		assertFalse(SpawnMove.plan(true, true, SpawnMove.FloorKind.BARRIER, false, "", "minecraft:barrier", MARKER).ok());
		assertFalse(SpawnMove.plan(true, true, SpawnMove.FloorKind.CONTAINER, false, "", "minecraft:chest", MARKER).ok());
		assertFalse(SpawnMove.plan(true, true, SpawnMove.FloorKind.NOT_FULL, false, "", "minecraft:oak_fence", MARKER).ok());
	}

	@Test
	void spawnMoveRestoresPreviousBlockAndNeverDuplicatesMarker() {
		SpawnMove.Result first = SpawnMove.plan(true, true, SpawnMove.FloorKind.SOLID, false, "minecraft:dirt", "minecraft:stone", MARKER);
		assertTrue(first.ok());
		assertTrue(first.restorePrevious());
		assertEquals("minecraft:dirt", first.restoreKey());

		SpawnMove.Result second = SpawnMove.plan(true, true, SpawnMove.FloorKind.SOLID, false, "minecraft:stone", "minecraft:deepslate", MARKER);
		assertEquals("minecraft:stone", second.restoreKey());
		assertFalse(second.restoreKey().equals(MARKER));

		SpawnMove.Result markerSaved = SpawnMove.plan(true, true, SpawnMove.FloorKind.SOLID, false, MARKER, "minecraft:stone", MARKER);
		assertEquals("minecraft:air", markerSaved.restoreKey());

		SpawnMove.Result same = SpawnMove.plan(true, true, SpawnMove.FloorKind.MARKER, true, "minecraft:dirt", MARKER, MARKER);
		assertFalse(same.ok());
		assertFalse(same.restorePrevious());
	}

	private static final String MARKER = "personnalworld:spawn_marker";
}
