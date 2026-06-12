rootProject.name = "build-logic"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.whereareiam.me/release")
        maven("https://maven.whereareiam.me/development")
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.whereareiam.me/release")
        maven("https://maven.whereareiam.me/development")
        maven("https://repo.codemc.io/repository/maven-releases/")
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
