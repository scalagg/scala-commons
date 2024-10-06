plugins {
    id("io.papermc.paperweight.userdev") version "1.7.3"
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("gg.scala.commons:bukkit:3.4.5")
    compileOnly("gg.scala.cloudsync:spigot:1.0.1")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}
