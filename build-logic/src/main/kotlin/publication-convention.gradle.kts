plugins {
    id("org.gradle.maven-publish")
}

publishing {
    repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
        credentials {
            username = System.getenv("OSSRH_USER")
            password = System.getenv("OSSRH_KEY")
        }
    }

    publications.withType<MavenPublication> {
        pom {
            name.set("VideoPlayer Jetpack compose for KMP")
            description.set("VideoPlayer")
            url.set("https://github.com/estivensh4/VideoPlayerKMP")
            licenses {
                license {
                    name.set("Apache-2.0")
                    distribution.set("repo")
                    url.set("https://github.com/estivensh4/VideoPlayerKMP/blob/master/LICENSE.md")
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
                connection.set("scm:git:ssh://github.com/estivensh4/VideoPlayerKMP.git")
                developerConnection.set("scm:git:ssh://github.com/estivensh4/VideoPlayerKMP.git")
                url.set("https://github.com/estivensh4/VideoPlayerKMP")
            }
        }
    }
}


apply(plugin = "signing")

configure<SigningExtension> {
    val signingKey: String? = System.getenv("SIGNING_KEY")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

val signingTasks = tasks.withType<Sign>()
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(signingTasks)
}