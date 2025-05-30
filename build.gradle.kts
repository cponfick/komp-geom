import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.spotless)
  alias(libs.plugins.kotest.multiplatform)
}

group = "io.github.cponfick"

version = "0.0.1"

repositories { mavenCentral() }

kotlin {
  jvm()
  //  linuxX64()
  js(IR) { nodejs() }

  // Configure JVM toolchain at extension level as required by error message
  jvmToolchain(21)

  sourceSets {
    val commonMain by getting
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(libs.kotest.assertions.core)
      }
    }
    val jvmMain by getting
    val jvmTest by getting

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
    ktfmt(libs.versions.ktfmt.get()).googleStyle()
  }
}
