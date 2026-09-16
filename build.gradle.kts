plugins {
	id("dev.architectury.loom")
	id("architectury-plugin")
}

val minecraft = stonecutter.current.version

version = "${mod.version}+$minecraft"
group = "${mod.group}.common"
base {
	archivesName.set("${mod.id}-common")
}

architectury {
	common("fabric", "neoforge")
}

repositories {
	maven("https://maven.architectury.dev/")
	maven("https://api.modrinth.com/maven")
	maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") {
		name = "GeckoLib"
		content { includeGroup("software.bernie.geckolib") }
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraft")
	mappings("net.fabricmc:yarn:$minecraft+build.${mod.dep("yarn_build")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
	modCompileOnly("dev.architectury:architectury:${mod.dep("architectury")}")
	modCompileOnly(files(rootProject.file("libs/darchitect-fabric.jar")))
	// Présent à la compilation seulement : le jar publié ne l’exige pas.
	modCompileOnly("software.bernie.geckolib:geckolib-fabric-1.21.1:${mod.dep("geckolib")}")
}

loom {
	decompilers {
		get("vineflower").apply {
			options.put("mark-corresponding-synthetics", "1")
		}
	}
	mixin {
		useLegacyMixinAp.set(true)
		defaultRefmapName.set("personnalworld-common-refmap.json")
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

tasks.build {
	group = "versioned"
	description = "Must run through 'chiseledBuild'"
}

tasks.register("buildAndCollect") {
	group = "versioned"
	dependsOn("build")
}
