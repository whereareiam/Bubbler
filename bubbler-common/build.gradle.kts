dependencies {
    "compileOnly"(project(":bubbler-api"))
}

tasks.test {
    useJUnitPlatform()
}