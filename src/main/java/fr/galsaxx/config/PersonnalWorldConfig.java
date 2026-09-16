package fr.galsaxx.config;

import dev.architectury.platform.Platform;
import fr.galsaxx.PersonnalWorld;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Server config ({@code config/personnalworld.toml}). Written with EN comments on first run.
 */
public final class PersonnalWorldConfig {
	private static final String FILE_NAME = "personnalworld.toml";
	private static final Pattern LIST_PATTERN = Pattern.compile(
			"^\\s*([A-Za-z][A-Za-z0-9_]*)\\s*=\\s*\\[(.*?)]\\s*$",
			Pattern.DOTALL);
	private static final Pattern INT_PATTERN = Pattern.compile(
			"^\\s*([A-Za-z][A-Za-z0-9_]*)\\s*=\\s*(-?\\d+)\\s*$");
	private static final Pattern BOOL_PATTERN = Pattern.compile(
			"^\\s*([A-Za-z][A-Za-z0-9_]*)\\s*=\\s*(true|false)\\s*$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern STRING_IN_LIST = Pattern.compile("\"([^\"]+)\"");

	private static final String DEFAULT_TEMPLATE = """
			# =============================================================================
			# PersonnalWorld — server config
			# File: config/personnalworld.toml (created automatically if missing)
			# =============================================================================
			# Dimension IDs must be full identifiers: "namespace:path"
			# Examples: "minecraft:overworld", "minecraft:the_nether", "mymod:dungeon"
			#
			# After editing this file, restart the server (or reload if supported later).
			# =============================================================================

			# -----------------------------------------------------------------------------
			# noDimensionSavePosition
			# -----------------------------------------------------------------------------
			# When you use the staff to go TO your personal world, your current position is
			# normally saved so you can return later.
			# List dimensions here where that save must NOT happen.
			# Default: empty (save from every dimension that is not your personal world).
			#
			# Example (uncomment and edit to use):
			# noDimensionSavePosition = [
			#   "minecraft:the_end",
			#   "mymod:arena",
			# ]
			noDimensionSavePosition = []

			# -----------------------------------------------------------------------------
			# noDimensionTeleport
			# -----------------------------------------------------------------------------
			# List dimensions where right-clicking the staff must NOT teleport you to your
			# personal world (blocked). Useful for event / minigame / dungeon dimensions.
			# Default: empty (teleport allowed from everywhere except when already inside
			# your personal world — that case returns you home instead).
			#
			# Example (uncomment and edit to use):
			# noDimensionTeleport = [
			#   "mymod:dungeon",
			#   "mymod:minigame",
			# ]
			noDimensionTeleport = []

			# -----------------------------------------------------------------------------
			# staffCooldownTicks
			# -----------------------------------------------------------------------------
			# Cooldown applied to the personal-world staff after a successful use, in ticks
			# (20 ticks = 1 second). Default matches the previous hard-coded value (40 = 2s).
			#
			# Example:
			# staffCooldownTicks = 60
			staffCooldownTicks = 40

			# -----------------------------------------------------------------------------
			# shareInventory
			# -----------------------------------------------------------------------------
			# If true, inventory / player data is shared between the Overworld and personal
			# worlds (DimensionArchitect isolatePlayerData = false at dimension creation).
			# If false, each personal world keeps an isolated inventory.
			# Default: true (current PersonnalWorld behaviour).
			#
			# Note: only affects dimensions created AFTER this setting is applied.
			#
			# Example:
			# shareInventory = false
			shareInventory = true
			""";

	private static volatile PersonnalWorldConfig INSTANCE = defaults();

	private final Set<String> noDimensionSavePosition;
	private final Set<String> noDimensionTeleport;
	private final int staffCooldownTicks;
	private final boolean shareInventory;

	private PersonnalWorldConfig(
			Set<String> noDimensionSavePosition,
			Set<String> noDimensionTeleport,
			int staffCooldownTicks,
			boolean shareInventory) {
		this.noDimensionSavePosition = noDimensionSavePosition;
		this.noDimensionTeleport = noDimensionTeleport;
		this.staffCooldownTicks = Math.max(0, staffCooldownTicks);
		this.shareInventory = shareInventory;
	}

	public static PersonnalWorldConfig get() {
		return INSTANCE;
	}

	public static void load() {
		Path path = Platform.getConfigFolder().resolve(FILE_NAME);
		try {
			if (Files.notExists(path)) {
				Files.createDirectories(path.getParent());
				Files.writeString(path, DEFAULT_TEMPLATE, StandardCharsets.UTF_8);
				INSTANCE = defaults();
				PersonnalWorld.LOGGER.info("Created default config {}", path);
				return;
			}
			String raw = Files.readString(path, StandardCharsets.UTF_8);
			INSTANCE = parse(raw);
			PersonnalWorld.LOGGER.info("Loaded config {}", path);
		} catch (IOException e) {
			PersonnalWorld.LOGGER.error("Failed to load {}; using defaults", path, e);
			INSTANCE = defaults();
		}
	}

	public boolean isNoSavePosition(String dimensionId) {
		return noDimensionSavePosition.contains(dimensionId);
	}

	public boolean isNoTeleport(String dimensionId) {
		return noDimensionTeleport.contains(dimensionId);
	}

	public int staffCooldownTicks() {
		return staffCooldownTicks;
	}

	public boolean shareInventory() {
		return shareInventory;
	}

	private static PersonnalWorldConfig defaults() {
		return new PersonnalWorldConfig(Set.of(), Set.of(), 40, true);
	}

	static PersonnalWorldConfig parse(String raw) {
		Set<String> noSave = new HashSet<>();
		Set<String> noTp = new HashSet<>();
		int cooldown = 40;
		boolean share = true;

		for (String logicalLine : splitLogicalLines(stripComments(raw))) {
			Matcher list = LIST_PATTERN.matcher(logicalLine);
			if (list.matches()) {
				String key = list.group(1);
				List<String> values = parseStringList(list.group(2));
				if ("noDimensionSavePosition".equals(key)) {
					noSave.clear();
					noSave.addAll(values);
				} else if ("noDimensionTeleport".equals(key)) {
					noTp.clear();
					noTp.addAll(values);
				}
				continue;
			}
			Matcher intM = INT_PATTERN.matcher(logicalLine);
			if (intM.matches() && "staffCooldownTicks".equals(intM.group(1))) {
				cooldown = Integer.parseInt(intM.group(2));
				continue;
			}
			Matcher boolM = BOOL_PATTERN.matcher(logicalLine);
			if (boolM.matches() && "shareInventory".equals(boolM.group(1))) {
				share = Boolean.parseBoolean(boolM.group(2).toLowerCase(Locale.ROOT));
			}
		}

		return new PersonnalWorldConfig(
				Collections.unmodifiableSet(noSave),
				Collections.unmodifiableSet(noTp),
				cooldown,
				share);
	}

	/** Drop # comments; keep quoted strings intact. */
	private static String stripComments(String raw) {
		StringBuilder out = new StringBuilder(raw.length());
		boolean inString = false;
		for (int i = 0; i < raw.length(); i++) {
			char c = raw.charAt(i);
			if (c == '"' && (i == 0 || raw.charAt(i - 1) != '\\')) {
				inString = !inString;
				out.append(c);
				continue;
			}
			if (!inString && c == '#') {
				while (i < raw.length() && raw.charAt(i) != '\n') {
					i++;
				}
				if (i < raw.length()) {
					out.append('\n');
				}
				continue;
			}
			out.append(c);
		}
		return out.toString();
	}

	/** Join multi-line [...] arrays into single logical lines. */
	private static List<String> splitLogicalLines(String text) {
		List<String> lines = new ArrayList<>();
		StringBuilder buf = new StringBuilder();
		int bracketDepth = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '[') {
				bracketDepth++;
			} else if (c == ']' && bracketDepth > 0) {
				bracketDepth--;
			}
			if (c == '\n' && bracketDepth == 0) {
				String line = buf.toString().trim();
				if (!line.isEmpty()) {
					lines.add(line);
				}
				buf.setLength(0);
			} else if (c != '\r') {
				buf.append(c);
			}
		}
		String last = buf.toString().trim();
		if (!last.isEmpty()) {
			lines.add(last);
		}
		return lines;
	}

	private static List<String> parseStringList(String inner) {
		List<String> values = new ArrayList<>();
		Matcher m = STRING_IN_LIST.matcher(inner);
		while (m.find()) {
			values.add(m.group(1));
		}
		return values;
	}
}
