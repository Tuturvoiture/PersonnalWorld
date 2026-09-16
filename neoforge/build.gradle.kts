@file:Suppress("UnstableApiUsage")

plugins {
	id("dev.architectury.loom")
	id("architectury-plugin")
	id("com.github.johnrengelman.shadow")
}

val loader = prop("loom.platform")!!
val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")?.project) {
	"No common project for $project"
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.$loader"
base {
	archivesName.set("${mod.id}-$loader")
}

architectury {
	platformSetupLoomIde()
	neoForge()
}

val commonBundle: Configuration by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
}

configurations {
	compileClasspath.get().extendsFrom(commonBundle)
	runtimeClasspath.get().extendsFrom(commonBundle)
	get("developmentNeoForge").extendsFrom(commonBundle)
}

repositories {
	maven("https://maven.neoforged.net/releases/")
	maven("https://maven.architectury.dev/")
	maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") {
		name = "GeckoLib"
		content { includeGroup("software.bernie.geckolib") }
	}
	mavenCentral()
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraft")
	mappings(loom.layered {
		mappings("net.fabricmc:yarn:$minecraft+build.${common.mod.dep("yarn_build")}:v2")
		common.mod.dep("neoforge_patch").takeUnless { it.startsWith("[") }?.let {
			mappings("dev.architectury:yarn-mappings-patch-neoforge:$it")
		}
	})
	"neoForge"("net.neoforged:neoforge:${common.mod.dep("neoforge_loader")}")
	modImplementation("dev.architectury:architectury-neoforge:${mod.dep("architectury")}")
	modImplementation(files(rootProject.file("libs/darchitect-neoforge.jar")))
	modCompileOnly("software.bernie.geckolib:geckolib-neoforge-1.21.1:${common.mod.dep("geckolib")}")
	modLocalRuntime("software.bernie.geckolib:geckolib-neoforge-1.21.1:${common.mod.dep("geckolib")}")

	commonBundle(project(path = common.path, configuration = "namedElements")) { isTransitive = false }
	shadowBundle(project(path = common.path, configuration = "transformProductionNeoForge")) { isTransitive = false }
}

loom {
	decompilers {
		get("vineflower").apply {
			options.put("mark-corresponding-synthetics", "1")
		}
	}

	runConfigs.all {
		isIdeConfigGenerated = true
		runDir = "../../run"
		vmArgs("-Dmixin.debug.export=true")
	}
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 21
}

tasks.jar {
	archiveClassifier = "dev"
	from(rootProject.file("LICENSE")) {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

tasks.remapJar {
	injectAccessWidener.set(true)
	inputFile.set(tasks.shadowJar.get().archiveFile)
	archiveClassifier.set("")
	dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
	configurations = listOf(shadowBundle)
	archiveClassifier = "dev-shadow"
	exclude("fabric.mod.json", "architectury.common.json")
}

tasks.processResources {
	properties(
		listOf("META-INF/neoforge.mods.toml", "pack.mcmeta"),
		"id" to mod.id,
		"name" to mod.name,
		"version" to mod.version,
		"minecraft" to common.mod.prop("mc_dep_forgelike")
	)
}

tasks.build {
	group = "versioned"
	description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
	group = "versioned"
	description = "Must run through 'chiseledBuild'"
	from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
	dependsOn("build")
}
