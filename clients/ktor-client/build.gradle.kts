@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `maven-publish`
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

dependencies {
  api(projects.core)
  api(libs.ktor.client.core)

  testImplementation(projects.core)
  testImplementation(libs.ktor.client.cio)
  testImplementation(projects.test)
  testImplementation(libs.coroutines.core)
  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.kotest.property)
  testImplementation(libs.kotest.runnerJUnit5)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = "com.fortysevendegrees.arrow-endpoint"
      artifactId = "client-ktor"
      version = "0.0.1"

      from(components["java"])
    }
  }
}
