package fr.galsaxx.build

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Archive PATCHNOTES / META pour `build/libs/<ver>/` et `builds/<ver>/`.
 *
 * - **alpha** : bullets de cette version seule (delta depuis l’alpha précédente).
 * - **beta / stable (official)** : agrégat depuis la dernière version **du même canal**,
 *   avec dédoublonnage thématique (ex. 3 alphas spawn_marker → 1 ligne = dernière).
 */
object VersionArchive {
	private val bulletRegex =
		Regex("""^-\s+(\d+\.\d+\.\d+(?:-(?:alpha|beta)\.\d+)?)\s+[\p{Pd}\-—–―]\s+(.+)$""")

	fun registerRootTasks(root: Project) {
		root.tasks.register<WritePatchNotesTask>("writePendingPatchNotes") {
			group = "versioning"
			description =
				"Génère PATCHNOTES/META pour mod.version courant (build/libs + builds) depuis CHANGELOG_WIP"
			modVersion.set(root.providers.gradleProperty("mod.version"))
			onlyCurrent.set(true)
		}
		root.tasks.register<WritePatchNotesTask>("generateAllLibsPatchNotes") {
			group = "versioning"
			description =
				"Génère PATCHNOTES pour chaque dossier déjà présent sous build/libs/<version>/"
			modVersion.set(root.providers.gradleProperty("mod.version"))
			onlyCurrent.set(false)
		}
	}

	fun wireBuildAndCollect(project: Project) {
		val root = project.rootProject
		project.tasks.matching { it.name == "buildAndCollect" }.configureEach {
			finalizedBy(root.tasks.named("writePendingPatchNotes"))
		}
	}

	data class ModVer(
		val major: Int,
		val minor: Int,
		val patch: Int,
		val channel: Channel,
		val pre: Int,
		val raw: String,
	) : Comparable<ModVer> {
		enum class Channel { ALPHA, BETA, STABLE }

		override fun compareTo(other: ModVer): Int {
			compareValuesBy(this, other, { it.major }, { it.minor }, { it.patch }).let {
				if (it != 0) return it
			}
			compareValuesBy(this, other, { it.channel.ordinal }, { it.pre }).let {
				if (it != 0) return it
			}
			return 0
		}

		companion object {
			fun parse(raw: String): ModVer? {
				val t = raw.trim()
				val m = Regex("""^(\d+)\.(\d+)\.(\d+)(?:-(alpha|beta)\.(\d+))?$""").matchEntire(t)
					?: return null
				val channel = when (m.groupValues[4]) {
					"alpha" -> Channel.ALPHA
					"beta" -> Channel.BETA
					else -> Channel.STABLE
				}
				val pre = m.groupValues[5].toIntOrNull() ?: 0
				return ModVer(
					m.groupValues[1].toInt(),
					m.groupValues[2].toInt(),
					m.groupValues[3].toInt(),
					channel,
					pre,
					t,
				)
			}
		}
	}

	data class Bullet(val version: ModVer, val text: String)

	fun parseWip(wip: File): List<Bullet> {
		if (!wip.isFile) return emptyList()
		return wip.readLines()
			.mapNotNull { line ->
				val m = bulletRegex.matchEntire(line.trim()) ?: return@mapNotNull null
				val ver = ModVer.parse(m.groupValues[1]) ?: return@mapNotNull null
				Bullet(ver, m.groupValues[2].trim())
			}
	}

	fun previousSameChannel(current: ModVer, known: Collection<ModVer>): ModVer? {
		return known
			.filter { it.channel == current.channel && it < current }
			.maxOrNull()
	}

	fun previousAny(current: ModVer, known: Collection<ModVer>): ModVer? {
		return known.filter { it < current }.maxOrNull()
	}

	fun topicKey(text: String): String {
		val t = text.lowercase()
		return when {
			"spawn_marker" in t || ("spawn" in t && "marker" in t) -> "spawn_marker"
			"geckolib" in t || "bâton" in t || "baton" in t -> "staff_geckolib"
			"façade ui" in t || "facade ui" in t || "islandmembersapi" in t ||
				"s2c" in t || "livre" in t && "ui" in t -> "ui_sync"
			"/pw" in t || "invite" in t || "whitelist" in t ||
				("temp" in t && ("visit" in t || "guest" in t || "kick" in t)) ||
				("droit" in t && ("île" in t || "ile" in t || "accès" in t || "acces" in t)) ->
				"invites_access"
			"darchitect" in t || "dimensionarchitect" in t -> "darchitect"
			"tomlj" in t -> "tomlj"
			"inventaire" in t || "shareinventory" in t || "isolateplayerdata" in t -> "inventory"
			"returnworld" in t || t.startsWith("retour") || ("retour" in t && ("dim" in t || "monde" in t || "save" in t)) ->
				"return"
			"dimlib" in t -> "dimlib"
			"hors-scope" in t || "hors scope" in t -> "hors_scope_doc"
			"personnalworld.toml" in t || ("config" in t && "toml" in t) -> "config_toml"
			"init île" in t || "init ile" in t || ("synchrone" in t && "tp" in t) -> "island_init"
			else -> {
				val words = t.replace(Regex("""[^\p{L}\p{N}\s]"""), " ")
					.split(Regex("""\s+"""))
					.filter { it.length > 2 }
					.take(5)
				if (words.isEmpty()) "misc_${text.hashCode()}" else "misc_" + words.joinToString("_")
			}
		}
	}

	/** Alpha : bullets exactes. Beta/stable : plage ]prevSame ; current] dédoublonnée par topic. */
	fun selectBullets(
		current: ModVer,
		all: List<Bullet>,
		knownVersions: Collection<ModVer>,
	): Pair<ModVer?, List<String>> {
		return when (current.channel) {
			ModVer.Channel.ALPHA -> {
				val prev = previousAny(current, knownVersions)
				val lines = all.filter { it.version == current }.map { it.text }
				prev to lines
			}
			ModVer.Channel.BETA, ModVer.Channel.STABLE -> {
				val prev = previousSameChannel(current, knownVersions)
				val inRange = all.filter { b ->
					b.version <= current && (prev == null || b.version > prev)
				}
				val deduped = LinkedHashMap<String, Bullet>()
				for (b in inRange.sortedBy { it.version }) {
					deduped[topicKey(b.text)] = b
				}
				val lines = deduped.values
					.sortedByDescending { it.version }
					.map { it.text }
				prev to lines
			}
		}
	}

	fun renderPatchNotes(
		version: ModVer,
		since: ModVer?,
		bullets: List<String>,
		gitRev: String,
		builtAt: String,
	): String {
		val channelLabel = when (version.channel) {
			ModVer.Channel.ALPHA -> "alpha (delta de cette version)"
			ModVer.Channel.BETA ->
				"beta (agrégat depuis la dernière beta, sans doublons thématiques)"
			ModVer.Channel.STABLE ->
				"official/stable (agrégat depuis la dernière stable, sans doublons thématiques)"
		}
		val sinceLine = when {
			since != null && version.channel == ModVer.Channel.ALPHA ->
				"- **Depuis :** `${since.raw}` (version précédente)"
			since != null ->
				"- **Depuis :** `${since.raw}` (dernier canal ${version.channel.name.lowercase()})"
			else ->
				"- **Depuis :** _(aucune version antérieure du même canal)_"
		}
		val body = if (bullets.isEmpty()) {
			"_Aucun bullet WIP trouvé pour cette plage — compléter `docs/CHANGELOG_WIP.md`._"
		} else {
			bullets.joinToString("\n") { "- $it" }
		}
		return buildString {
			appendLine("# Patch notes — ${version.raw}")
			appendLine()
			appendLine("- **Canal :** $channelLabel")
			appendLine(sinceLine)
			appendLine("- **Build (UTC) :** $builtAt")
			appendLine("- **Git :** `$gitRev`")
			appendLine("- **Capacités :** voir `docs/CAPABILITIES.md`")
			appendLine("- **Source :** `docs/CHANGELOG_WIP.md` (Pending)")
			appendLine()
			appendLine("---")
			appendLine()
			appendLine("## Changements")
			appendLine()
			appendLine(body)
			appendLine()
		}
	}

	fun renderMeta(version: String, gitRev: String, builtAt: String, loaders: List<String>): String {
		return buildString {
			appendLine("# META — $version")
			appendLine()
			appendLine("- **Built (UTC) :** $builtAt")
			appendLine("- **Git :** `$gitRev`")
			appendLine("- **Loaders :** ${loaders.joinToString(", ").ifEmpty { "_(none yet)_" }}")
			appendLine()
		}
	}

	fun gitRev(root: File): String {
		return try {
			val p = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
				.directory(root)
				.redirectErrorStream(true)
				.start()
			val out = p.inputStream.bufferedReader().readText().trim()
			if (p.waitFor() == 0 && out.isNotEmpty()) out else "unknown"
		} catch (_: Exception) {
			"unknown"
		}
	}

	fun updateIndex(buildsRoot: File, version: String, builtAt: String) {
		val index = File(buildsRoot, "INDEX.md")
		val header = """
			|# Archive des builds
			|
			|Historique durable par `mod.version` — **hors** `build/` Gradle (éphémère).
			|
			|| Version | Dernier build (UTC) | Notes |
			||---------|---------------------|-------|
		""".trimMargin()
		val rowRegex = Regex("""^\|\s*([^|]+?)\s*\|\s*([^|]+?)\s*\|\s*(.+)\s*\|$""")
		val rows = linkedMapOf<String, String>()
		if (index.isFile) {
			index.readLines().forEach { line ->
				val m = rowRegex.matchEntire(line.trim()) ?: return@forEach
				val ver = m.groupValues[1].trim()
				if (ver == "Version" || ver.startsWith("---")) return@forEach
				rows[ver] = line.trim()
			}
		}
		rows[version] =
			"| $version | $builtAt | [PATCHNOTES]($version/PATCHNOTES.md) · [META]($version/META.md) |"
		val sorted = rows.entries.sortedByDescending {
			ModVer.parse(it.key) ?: ModVer(0, 0, 0, ModVer.Channel.ALPHA, 0, it.key)
		}
		index.writeText(
			buildString {
				appendLine(header)
				sorted.forEach { appendLine(it.value) }
				appendLine()
				appendLine("Voir [`docs/VERSIONING.md`](../docs/VERSIONING.md) § Archive `builds/`.")
				appendLine()
			},
		)
	}

	fun writeForVersion(
		rootDir: File,
		versionRaw: String,
		allBullets: List<Bullet>,
		knownFromLibs: Collection<ModVer>,
		git: String,
		builtAt: String,
	) {
		val current = ModVer.parse(versionRaw) ?: return
		val known = (knownFromLibs + allBullets.map { it.version } + current).distinctBy { it.raw }
		val (since, lines) = selectBullets(current, allBullets, known)
		val notes = renderPatchNotes(current, since, lines, git, builtAt)

		val libsDir = File(rootDir, "build/libs/$versionRaw")
		libsDir.mkdirs()
		File(libsDir, "PATCHNOTES.md").writeText(notes)

		val fabricDir = File(libsDir, "fabric")
		val neoDir = File(libsDir, "neoforge")
		val loaders = buildList {
			if (fabricDir.isDirectory && fabricDir.listFiles()?.any { it.extension == "jar" } == true) {
				add("fabric")
			}
			if (neoDir.isDirectory && neoDir.listFiles()?.any { it.extension == "jar" } == true) {
				add("neoforge")
			}
		}
		val meta = renderMeta(versionRaw, git, builtAt, loaders)
		File(libsDir, "META.md").writeText(meta)

		val buildsDir = File(rootDir, "builds/$versionRaw")
		buildsDir.mkdirs()
		File(buildsDir, "PATCHNOTES.md").writeText(notes)
		File(buildsDir, "META.md").writeText(meta)
		updateIndex(File(rootDir, "builds"), versionRaw, builtAt)
	}
}

abstract class WritePatchNotesTask : DefaultTask() {
	@get:Input
	abstract val modVersion: Property<String>

	@get:Input
	abstract val onlyCurrent: Property<Boolean>

	@TaskAction
	fun run() {
		val rootDir = project.rootProject.projectDir
		val wip = File(rootDir, "docs/CHANGELOG_WIP.md")
		val bullets = VersionArchive.parseWip(wip)
		val libsRoot = File(rootDir, "build/libs")
		val fromLibs = libsRoot.listFiles()
			?.filter { it.isDirectory }
			?.mapNotNull { VersionArchive.ModVer.parse(it.name) }
			.orEmpty()
		val git = VersionArchive.gitRev(rootDir)
		val builtAt = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

		val targets = if (onlyCurrent.get()) {
			listOf(modVersion.get())
		} else {
			fromLibs.map { it.raw }.sortedWith { a, b ->
				val va = VersionArchive.ModVer.parse(a)
				val vb = VersionArchive.ModVer.parse(b)
				when {
					va != null && vb != null -> va.compareTo(vb)
					else -> a.compareTo(b)
				}
			}
		}

		if (targets.isEmpty()) {
			logger.lifecycle("No versions to annotate under build/libs")
			return
		}

		for (ver in targets) {
			VersionArchive.writeForVersion(rootDir, ver, bullets, fromLibs, git, builtAt)
			logger.lifecycle("PATCHNOTES written for $ver")
		}
	}
}
