plugins {
	id("dev.kikugie.stonecutter")
	id("dev.architectury.loom") version "1.10.455" apply false
	id("architectury-plugin") version "3.4-SNAPSHOT" apply false
	id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

stonecutter active "1.21.1" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
	group = "project"
	ofTask("buildAndCollect")
}

for (branch in stonecutter.tree.branches) {
	if (branch.id.isEmpty()) continue
	val loader = branch.id.replaceFirstChar { it.uppercaseChar() }
	stonecutter registerChiseled tasks.register("chiseledBuild$loader", stonecutter.chiseled) {
		group = "project"
		versions { projectBranch, _ -> projectBranch == branch.id }
		ofTask("buildAndCollect")
	}
}
