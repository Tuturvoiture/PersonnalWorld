package fr.galsaxx.invite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Validates access JSON / records before use.
 */
public final class AccessFileValidator {
	private AccessFileValidator() {}

	public static ValidationResult validate(AccessRecord record, String expectedDimensionId) {
		List<String> errors = new ArrayList<>();
		if (record == null) {
			return ValidationResult.invalid(List.of("record is null"));
		}
		if (record.schemaVersion() != AccessRecord.SCHEMA_VERSION) {
			errors.add("unsupported schemaVersion=" + record.schemaVersion());
		}
		if (record.revision() < 1L) {
			errors.add("revision must be >= 1");
		}
		if (record.dimensionId() == null || record.dimensionId().isBlank()) {
			errors.add("dimensionId missing");
		} else if (expectedDimensionId != null && !expectedDimensionId.equals(record.dimensionId())) {
			errors.add("dimensionId mismatch: file=" + record.dimensionId() + " expected=" + expectedDimensionId);
		} else if (!IslandIds.isPersonalIsland(record.dimensionId())) {
			errors.add("dimensionId is not a personnalworld perso island: " + record.dimensionId());
		}
		if (record.ownerUuid() == null) {
			errors.add("ownerUuid missing");
		}
		Set<UUID> seen = new HashSet<>();
		for (AccessRecord.Member m : record.members().values()) {
			if (m.uuid == null) {
				errors.add("member uuid null");
				continue;
			}
			if (!seen.add(m.uuid)) {
				errors.add("duplicate member uuid " + m.uuid);
			}
			if (record.ownerUuid() != null && m.uuid.equals(record.ownerUuid())) {
				errors.add("owner listed in members");
			}
			if (m.role == null) {
				errors.add("member role null for " + m.uuid);
			} else if (m.role == IslandRole.OWNER || m.role == IslandRole.TEMP_VISITOR) {
				errors.add("illegal persisted role " + m.role + " for " + m.uuid);
			} else if (!m.role.isPersistentWhitelist() && m.role != IslandRole.BANNED) {
				errors.add("unknown member role " + m.role);
			}
			// BANNED is allowed in schema/read path for forward compatibility.
		}
		if (errors.isEmpty()) {
			return ValidationResult.valid();
		}
		return ValidationResult.invalid(errors);
	}

	public static IslandRole parsePersistedRole(String raw) {
		if (raw == null) {
			return null;
		}
		String key = raw.trim().toUpperCase(Locale.ROOT);
		try {
			IslandRole role = IslandRole.valueOf(key);
			if (role == IslandRole.OWNER || role == IslandRole.TEMP_VISITOR) {
				return null;
			}
			return role;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public record ValidationResult(boolean ok, List<String> errors) {
		public static ValidationResult valid() {
			return new ValidationResult(true, List.of());
		}

		public static ValidationResult invalid(List<String> errors) {
			return new ValidationResult(false, List.copyOf(errors));
		}

		public String summary() {
			return String.join("; ", errors);
		}
	}
}
