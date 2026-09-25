plugins {
    java
}

group = "dev.caveslite"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
}

dependencies {
    // Purpur's API includes everything from Paper, Spigot and Bukkit, plus
    // Purpur's own extras (better mob AI options, more config, etc.).
    compileOnly("org.purpurmc.purpur:purpur-api:26.3.build.+")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.jar {
    archiveBaseName.set("DangerousCavesLite")
    from("LICENSE")
}
