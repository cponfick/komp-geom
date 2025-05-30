import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.spotless)
  alias(libs.plugins.kotest.multiplatform)
  alias(libs.plugins.maven.publish)
}

group = "io.github.cponfick"

version = "0.1.0-rc14"

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

mavenPublishing {
  publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

  signAllPublications()

  coordinates(group.toString(), "komp-geom", version.toString())

  pom {
    name = "Kotlin Computational Geometry"
    description = "A computational geometry library written in the kotlin multiplatform technology."
    inceptionYear = "2025"
    url = "https://github.com/cponfick/komp-geom"
    licenses {
      license {
        name = "MIT License"
        url = "https://github.com/cponfick/komp-geom/blob/main/LICENSE"
        distribution = "repo"
      }
    }
    developers {
      developer {
        id = "cponfick"
        name = "Constantin Ponfick"
        url = "https://github.com/cponfick"
      }
    }
    scm {
      url = "https://github.com/cponfick/komp-geom"
      connection = "scm:git:git://github.com/cponfick/komp-geom.git"
      developerConnection = "scm:git:ssh://github.com/cponfick/komp-geom.git"
    }
  }
}
