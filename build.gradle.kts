plugins {
    kotlin("jvm") version "1.9.23"
}

group = "at.orchaldir.gm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val loggingVersion = "3.0.5"
val logbackVersion = "1.5.6"

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$loggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
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