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
	fabric()
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
	get("developmentFabric").extendsFrom(commonBundle)
}

repositories {
	maven("https://maven.architectury.dev/")
	maven("https://api.modrinth.com/maven")
	maven("https://maven.fabricmc.net/")
	maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") {
		name = "GeckoLib"
		content { includeGroup("software.bernie.geckolib") }
	}
	mavenCentral()
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraft")
	mappings("net.fabricmc:yarn:$minecraft+build.${common.mod.dep("yarn_build")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${common.mod.dep("fabric_api")}")
	modImplementation("dev.architectury:architectury-fabric:${mod.dep("architectury")}")
	modImplementation(files(rootProject.file("libs/darchitect-fabric.jar")))
	modCompileOnly("software.bernie.geckolib:geckolib-fabric-1.21.1:${common.mod.dep("geckolib")}")
	modLocalRuntime("software.bernie.geckolib:geckolib-fabric-1.21.1:${common.mod.dep("geckolib")}")

	commonBundle(project(path = common.path, configuration = "namedElements")) { isTransitive = false }
	shadowBundle(project(path = common.path, configuration = "transformProductionFabric")) { isTransitive = false }
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

tasks.shadowJar {
	configurations = listOf(shadowBundle)
	archiveClassifier = "dev-shadow"
}

tasks.remapJar {
	injectAccessWidener.set(true)
	inputFile.set(tasks.shadowJar.get().archiveFile)
	archiveClassifier.set("")
	dependsOn(tasks.shadowJar)
}

tasks.jar {
	archiveClassifier = "dev"
	from(rootProject.file("LICENSE")) {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

tasks.processResources {
	properties(
		listOf("fabric.mod.json"),
		"version" to mod.version,
		"minecraft" to common.mod.prop("mc_dep_fabric")
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
