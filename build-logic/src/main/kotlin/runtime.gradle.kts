import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.jvm.tasks.Jar

plugins {
    id("shared")
    id("relocations")
}

tasks.withType<ShadowJar>().configureEach {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")

    val defaultDestination = rootProject.layout.buildDirectory.dir("libs")

    if (providers.gradleProperty("output").isPresent) {
        destinationDirectory.set(file(providers.gradleProperty("output").get()))
    } else {
        destinationDirectory.set(defaultDestination)
    }
}

tasks.named<Jar>("jar").configure {
    dependsOn(tasks.named("shadowJar"))
}

tasks.named<Copy>("processResources").configure {
    filesMatching("module.json") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "projectName" to rootProject.name,
                "projectVersion" to project.version
            )
        )
    }
}
