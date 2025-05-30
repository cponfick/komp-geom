import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.spotless)
  alias(libs.plugins.kotest.multiplatform)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.npm.publish)
}

group = "io.github.cponfick"

version = "0.1.0-rc4"

repositories { mavenCentral() }

kotlin {
  explicitApi()
  jvm()
  js(IR) {
    binaries.library()
    nodejs()
  }

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

publishing {
  repositories {
    maven {
      url = uri("https://maven.pkg.github.com/cponfick/komp-geom")
      credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }
}

npmPublish {
  packages { named("js") { packageJson { name.set("@cponfick/komp-geom") } } }

  registries {
    github {
      uri.set("https://npm.pkg.github.com")
      username = System.getenv("GITHUB_ACTOR")
      password = System.getenv("GITHUB_TOKEN")
    }
  }
}
