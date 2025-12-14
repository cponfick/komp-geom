@file:OptIn(ExperimentalWasmDsl::class)

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.kotlin.dsl.dokkaPlugin
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.spotless)
  alias(libs.plugins.kotest.multiplatform)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.dokka.html)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.sonarqube)
  alias(libs.plugins.kotlinx.benchmark)
}

group = "io.github.cponfick"

version = "0.4.0-rc1"

repositories { mavenCentral() }

// https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-hierarchy.html#default-hierarchy-template
// https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-dsl-reference.html#targets
kotlin {
  explicitApi()
  jvm {
    compilations {
      val main by getting
      val benchmark by creating { associateWith(main) }
    }
  }
  js(IR) {
    binaries.library()
    nodejs()
    browser { testTask { useKarma { useChromeHeadless() } } }
    compilations {
      val main by getting
      val benchmark by creating { associateWith(main) }
    }
  }
  wasmJs { browser { testTask { useKarma { useChromeHeadless() } } } }
  // Tier 1
  linuxX64 {
    compilations {
      val main by getting
      val benchmark by creating { associateWith(main) }
    }
  }
  macosX64()
  macosArm64()
  iosSimulatorArm64()
  iosX64()
  // Tier 2
  linuxArm64()
  watchosSimulatorArm64()
  watchosX64()
  watchosArm32()
  watchosArm64()
  tvosSimulatorArm64()
  tvosX64()
  tvosArm64()
  iosArm64()
  // Tier 3
  androidNativeArm32()
  androidNativeArm64()
  androidNativeX86()
  androidNativeX64()
  mingwX64()
  watchosDeviceArm64()

  applyDefaultHierarchyTemplate()

  sourceSets {
    val commonMain by getting { dependencies { implementation(libs.kotlin.logging) } }
    val jvmMain by getting { dependencies { implementation(libs.slf4j.simple) } }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(libs.kotest.assertions.core)
      }
    }
    val commonBenchmark by creating {
      dependencies { implementation(libs.kotlinx.benchmark.runtime) }
    }
    val jvmBenchmark by getting { dependsOn(commonBenchmark) }
    val jsBenchmark by getting { dependsOn(commonBenchmark) }
    val linuxX64Benchmark by getting { dependsOn(commonBenchmark) }
  }
}

tasks.withType<AbstractTestTask>().configureEach {
  testLogging {
    showStandardStreams = true
    showExceptions = true
    exceptionFormat = FULL
  }
}

tasks { withType<Test> { useJUnitPlatform() } }

spotless {
  kotlin {
    target("**/*.kt", "**/*.kts")
    ktfmt(libs.versions.ktfmt.get()).googleStyle()
    trimTrailingWhitespace()
    endWithNewline()
    toggleOffOn()
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    ktfmt(libs.versions.ktfmt.get()).googleStyle()
    trimTrailingWhitespace()
    endWithNewline()
  }
  format("misc") {
    target("**/*.md", "**/*.yaml", "**/*.yml")
    trimTrailingWhitespace()
    endWithNewline()
  }
}

mavenPublishing {
  publishToMavenCentral()

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

dependencies { dokkaPlugin("org.jetbrains.dokka:versioning-plugin:${libs.versions.dokka.get()}") }

dokka {
  dokkaSourceSets.commonMain {
    sourceLink { remoteUrl("https://github.com/cponfick/komp-geom/blob/${rootProject.version}") }
    includes.from("src/commonMain/kotlin/io/github/cponfick/kompgeom/kompgeom.md")
    moduleName.set("Kotlin Computational Geometry")
  }
  pluginsConfiguration {
    versioning {
      version.set(rootProject.version.toString())
      olderVersionsDir.set(projectDir.resolve("docs/dokka"))
      renderVersionsNavigationOnAllPages.set(true)
    }
  }
}

kover { reports { filters { excludes { classes("*Benchmark") } } } }

sonar {
  properties {
    property("sonar.projectKey", "cponfick_komp-geom")
    property("sonar.organization", "cponfick")
    property("sonar.host.url", "https://sonarcloud.io")
    val koverReport =
      allprojects
        .mapNotNull { project ->
          val reportPath = "${project.projectDir}/build/reports/kover/report.xml"
          if (File(reportPath).exists()) reportPath else null
        }
        .joinToString(",")
    property("sonar.coverage.jacoco.xmlReportPaths", koverReport)
  }
}

benchmark {
  targets {
    register("jvmBenchmark")
    register("jsBenchmark")
    register("linuxX64Benchmark")
  }
}
