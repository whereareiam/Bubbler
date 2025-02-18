defaultTasks("build", "shadowJar")

allprojects {
    version = (System.getenv("VERSION") ?: "dev")

    apply(plugin = "java")

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }
}

subprojects {
    repositories {
        mavenCentral()

        maven("https://jitpack.io")
        maven("https://repo.codemc.io/repository/maven-releases/")
    }

    dependencies {
        // lombok
        "compileOnly"(rootProject.libs.lombok)
        "annotationProcessor"(rootProject.libs.lombok)

        // general
        "compileOnly"(rootProject.libs.guice)
        "compileOnly"(rootProject.libs.adventure)
        "compileOnly"(rootProject.libs.socialismus)
        "compileOnly"(rootProject.libs.packetevents)
    }
}