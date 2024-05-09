plugins {
    kotlin("jvm") version "1.9.23"
}

group = "at.orchaldir.gm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.wrapper {
    gradleVersion = "8.5"
}

kotlin {
    jvmToolchain(17)
}