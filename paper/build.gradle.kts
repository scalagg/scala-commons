repositories {
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    api(project(":bukkit"))
    api(kotlin("stdlib"))
}
