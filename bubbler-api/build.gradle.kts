plugins {
    id("api")
}

toolkitPublish {
    artifactId.set("Bubbler")

    pom {
        name.set("Bubbler")
        description.set("Public API for Bubbler - Socialismus chat bubble module")
    }

    javadoc {
        title.set("Bubbler API")
        windowTitle.set("Bubbler API")
    }
}
