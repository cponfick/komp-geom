import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
  kotlin("multiplatform") version "2.1.20"
  id("com.diffplug.spotless") version "7.0.4"
  id("io.kotest.multiplatform") version "5.9.1"
}

group = "io.github.cponfick"

version = "0.0.1"

repositories { mavenCentral() }

kotlin {
  jvm()
  js(IR) { nodejs() }

  // Configure JVM toolchain at extension level as required by error message
  jvmToolchain(21)

  sourceSets {
    val commonMain by getting
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation("io.kotest:kotest-framework-engine:5.9.1")
        implementation("io.kotest:kotest-assertions-core:5.9.1")
        implementation("io.kotest:kotest-property:5.9.1")
      }
    }
    val jvmMain by getting
    val jvmTest by getting {
      dependencies {

      }
    }
    val jsMain by getting
    val jsTest by getting
  }
}

tasks {
  withType<Test> {
    useJUnitPlatform()
    testLogging {
      showStandardStreams = true
      showExceptions = true
      exceptionFormat = FULL
    }
  }
}

spotless {
  kotlin {
    target("src/**/*.kt")
    ktfmt("0.53").googleStyle()
  }
}
