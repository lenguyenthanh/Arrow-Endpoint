@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `maven-publish`
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

dependencies {
  api(projects.core)
  implementation(libs.coroutines.reactive)
  implementation(libs.coroutines.reactor)
  implementation(libs.spring.boot.starter.webflux)
  implementation(libs.reactor.kotlin.extensions)
  implementation(libs.netty.transport.native.kqueue)

  testImplementation(projects.test)
  testImplementation(projects.clients.springWebFluxClient)
  testImplementation(libs.undertow)
  testImplementation(rootProject.libs.coroutines.core)
  testImplementation(rootProject.libs.kotest.assertionsCore)
  testImplementation(rootProject.libs.kotest.property)
  testImplementation(rootProject.libs.kotest.runnerJUnit5)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = "com.fortysevendegrees.arrow-endpoint"
      artifactId = "spring-web-server"
      version = "0.0.1"

      from(components["java"])
    }
  }
}
