plugins {
    id("runtime")
}

dependencies {
    implementation(project(":bubbler-api"))
    implementation(project(":bubbler-command"))
    implementation(project(":bubbler-common"))
}
