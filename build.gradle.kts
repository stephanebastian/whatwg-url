plugins {
    `java-library`
    id("com.diffplug.spotless") version "6.21.0"
    id("me.champeau.jmh") version "0.7.1"
    id("com.palantir.git-version") version "3.0.0"
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
