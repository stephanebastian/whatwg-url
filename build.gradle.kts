plugins {
    `java-library`
    // plugin that takes care of formatting source code
    id("com.diffplug.spotless") version "6.21.0"
    // plugin that handles running Java Microbenchmark Harness
    id("me.champeau.jmh") version "0.7.1"
    // plugin that handles versioning
    id("com.palantir.git-version") version "3.0.0"
    // plugins to release on maven central
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

dependencies {
    implementation ("com.ibm.icu:icu4j:73.2")
    testImplementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

group = "com.stephanebastian"
val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

spotless {
    java {
        target(project.fileTree("./src").include("**/*.java"))
        // Use the default importOrder configuration
        importOrder()
        // remove unused imports
        removeUnusedImports()
        // use Eclipse JDT to format code (better than Google Format as it does not mess-up with internal classes not accessible with JAVA17 and up)
        eclipse().configFile(project.rootProject.file("./ide-styles/eclipse-java-google-style.xml"))
    }
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set("<<Component Description>>")
                url.set("https://github.com/stephanebastian/whatwg-url")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("stephanebastian")
                        name.set("Stephane Bastian")
                        email.set("stephane.bastian.dev@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/stephanebastian/whatwg-url.git")
                    connection.set("scm:git:git://github.com/stephanebastian/whatwg-url.git")
                    developerConnection.set("scm:git:git://github.com/stephanebastian/whatwg-url.git")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
