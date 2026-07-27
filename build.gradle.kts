import net.fabricmc.loom.api.LoomGradleExtensionAPI

val minecraftVersion: String = stonecutter.current.version
val fabricApiVersion = when (minecraftVersion) {
    "1.21.11" -> "0.141.5+1.21.11"
    else -> "0.155.2+$minecraftVersion"
}
val javaVersion = 25

// Fabric API 0.141.x (1.21.11) ships access wideners in the intermediary namespace,
// which the current Loom jar processor rejects. We do not rely on them.
val transitiveAccessWideners: Boolean = minecraftVersion != "1.21.11"
val obfuscatedMinecraft: Boolean = minecraftVersion.startsWith("1.")

// `net.fabricmc.fabric-loom` is the no-remap plugin: it disables obfuscation, which is
// correct for the deobfuscated 26.x jars but breaks the still-obfuscated 1.21.x ones.
apply(plugin = if (obfuscatedMinecraft) "net.fabricmc.fabric-loom-remap" else "net.fabricmc.fabric-loom")

val loom = extensions.getByType<LoomGradleExtensionAPI>()

group = "me.snowmii"
version = providers.gradleProperty("releaseVersion")
    .orElse("$minecraftVersion")
    .get()
base.archivesName = "resourceful"

dependencies {
    "minecraft"("com.mojang:minecraft:$minecraftVersion")
    // 26.x ships deobfuscated; 1.21.x still needs a mapping layer.
    if (obfuscatedMinecraft) {
        "mappings"(loom.officialMojangMappings())
    }
    // The remap plugin needs mods on `modImplementation` so Loom remaps them and pulls in
    // loader's own dependencies (sponge-mixin); the no-remap plugin has no such configuration.
    val modConfiguration = if (obfuscatedMinecraft) "modImplementation" else "implementation"
    modConfiguration("net.fabricmc:fabric-loader:0.19.3")
    modConfiguration("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

loom.apply {
    enableTransitiveAccessWideners = transitiveAccessWideners

    runs {
        named("client") {
            client()
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraftVersion)
    inputs.property("java_version", javaVersion)
    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to minecraftVersion,
            "java_version" to javaVersion,
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = javaVersion
}
