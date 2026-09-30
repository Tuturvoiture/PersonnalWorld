package fr.galsaxx.island;

/**
 * Pagination fixe de 3 îles. Ne connaît pas le plafond configuré.
 */
public final class IslandPageLayout {
	public static final int PAGE_SIZE = 3;

	/** Biais vertical : 0 = haut, 1 = plus bas. */
	public record Anchors(int leftBias, int centerBias, int rightBias) {}

	private IslandPageLayout() {}

	public static int pageCount(int islandCount) {
		if (islandCount <= 0) {
			return 1;
		}
		return Math.max(1, (islandCount + PAGE_SIZE - 1) / PAGE_SIZE);
	}

	public static String label(int page, int pages) {
		return page + "/" + pages;
	}

	public static int tryPrevious(int page, int pages) {
		if (pages <= 1 || page <= 1) {
			return page;
		}
		return page - 1;
	}

	public static int tryNext(int page, int pages) {
		if (pages <= 1 || page >= pages) {
			return page;
		}
		return page + 1;
	}

	public static int tryJump(int page, int pages, int target) {
		if (pages <= 1 || target < 1 || target > pages) {
			return page;
		}
		return target;
	}

	/** Page impaire = V (centre plus bas). Page paire = Λ (centre plus haut). */
	public static Anchors anchors(int page) {
		if ((page & 1) == 1) {
			return new Anchors(0, 1, 0);
		}
		return new Anchors(1, 0, 1);
	}

	public static int pageOfIsland(int indexZeroBased) {
		if (indexZeroBased < 0) {
			return 1;
		}
		return indexZeroBased / PAGE_SIZE + 1;
	}
}
