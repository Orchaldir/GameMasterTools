plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("org.sonarqube") version "4.0.0.2929"
    id("jacoco")
}

group = "at.orchaldir.gm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val ktorVersion = "2.3.10"
val loggingVersion = "3.0.5"
val logbackVersion = "1.5.6"

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$loggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:4.0.0.2929")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        xml.outputLocation.set(file("build/test-results/test/text.xml"))
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "Orchaldir_GameMasterTools")
        property("sonar.organization", "orchaldir-github")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.language", "kotlin")
        property("sonar.sources", "src/main/kotlin")
        property("sonar.tests", "src/test/kotlin")
        property("sonar.exclusions", "**/at/orchaldir/gm/app/**")
        property("sonar.junit.reportPaths", "build/test-results/test/text.xml")
        property("sonar.jacoco.reportPaths", "build/reports/jacoco/test")
    }
}

tasks.wrapper {
    gradleVersion = "8.0.2"
}

kotlin {
    jvmToolchain(17)
}