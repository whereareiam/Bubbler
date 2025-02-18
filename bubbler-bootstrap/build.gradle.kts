import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    "implementation"(project(":bubbler-configuration"))
    "implementation"(project(":bubbler-common-api"))
    "implementation"(project(":bubbler-command"))
    "implementation"(project(":bubbler-common"))
}

tasks.withType<ShadowJar> {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")

    relocate("com.google.inject", "me.whereareiam.socialismus.library.guice")
}

tasks.named<Copy>("processResources") {
    filter<ReplaceTokens>(
        "tokens" to mapOf(
            "projectName" to rootProject.name,
            "projectVersion" to project.version
        )
    )
}

tasks.named<Jar>("jar") {
    dependsOn("shadowJar")
}