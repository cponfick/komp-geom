plugins {
  kotlin("jvm") version "2.1.20"
  id("com.diffplug.spotless") version "7.0.3"
}

group = "com.github.cponfick"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
  testImplementation(kotlin("test"))
  testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
  testImplementation("io.kotest:kotest-assertions-core:5.9.1")
}

tasks.test { useJUnitPlatform() }

kotlin { jvmToolchain(21) }

spotless { kotlin { ktfmt("0.53").googleStyle() } }
