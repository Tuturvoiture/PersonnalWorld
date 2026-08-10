pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		maven("https://maven.kikugie.dev/releases")
		maven("https://maven.kikugie.dev/snapshots")
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.6"
}

stonecutter {
	centralScript = "build.gradle.kts"
	kotlinController = true
	create(rootProject) {
		// To add another MC version later:
		// 1) versions("1.21.1", "1.21.4")
		// 2) create versions/<mc>/gradle.properties (copy keys from 1.21.1)
		// 3) introduce //? if <condition> { ... //?} blocks where APIs diverge
		versions("1.21.1")
	}
}

rootProject.name = "PersonnalWorld"
