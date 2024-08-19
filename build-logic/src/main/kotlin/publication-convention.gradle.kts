import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("org.gradle.maven-publish")
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    // Define coordinates for the published artifact
    pom {
        name.set("Camera Jetpack compose for KMM")
        description.set("KaptureX")
        url.set("https://github.com/estivensh4/kaptureX")
        licenses {
            license {
                name.set("Apache-2.0")
                distribution.set("repo")
                url.set("https://github.com/estivensh4/kaptureX/blob/master/LICENSE.md")
            }
        }

        developers {
            developer {
                id.set("estivensh4")
                name.set("Estiven Sanchez")
                email.set("estivensh4@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:ssh://github.com/estivensh4/kaptureX.git")
            developerConnection.set("scm:git:ssh://github.com/estivensh4/kaptureX.git")
            url.set("https://github.com/estivensh4/kaptureX")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
