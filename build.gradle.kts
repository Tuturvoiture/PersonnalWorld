plugins {
	id("dev.kikugie.stonecutter")
	id("fabric-loom")
	id("maven-publish")
}

val minecraftVersion: String = stonecutter.current.version

version = "${property("mod.version")}+$minecraftVersion"
group = property("mod.group") as String

base {
	archivesName.set(property("mod.id") as String)
}

repositories {
	maven("https://api.modrinth.com/maven")
	maven("https://maven.fabricmc.net/")
	maven("https://maven.quiltmc.org/repository/release/")
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://maven.shedaniel.me/")
	maven("https://maven.ladysnake.org/releases")
}

loom {
	splitEnvironmentSourceSets()

	mods {
		create("personnalworld") {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings("net.fabricmc:yarn:${property("deps.yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
	modImplementation(files(rootProject.file("libs/dimlib-1.1.0+mc1.21.1.jar")))
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
}

tasks.processResources {
	val modVersion = project.version.toString()
	inputs.property("version", modVersion)

	filesMatching("fabric.mod.json") {
		expand("version" to modVersion)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 21
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

tasks.build {
	group = "versioned"
	description = "Prefer running via root chiseledBuild for multi-version builds"
}
