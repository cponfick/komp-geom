plugins {
  kotlin("jvm") version "2.1.20"
  id("com.diffplug.spotless") version "7.0.3"
}

group = "io.github.cponfick"

version = "0.0.1"

repositories { mavenCentral() }

dependencies {
  testImplementation(kotlin("test"))
  testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
  testImplementation("io.kotest:kotest-assertions-core:5.9.1")
}

tasks.test { useJUnitPlatform() }

kotlin { jvmToolchain(21) }

spotless { kotlin { ktfmt("0.53").googleStyle() } }
