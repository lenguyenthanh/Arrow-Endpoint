//apply(from = "https://raw.githubusercontent.com/arrow-kt/arrow/main/arrow-libs/gradle/publication.gradle")

plugins {
  `maven-publish`
}

dependencies {
  compileOnly(project(Libs.core))
  implementation(Libs.kotlinxCoroutinesReactive)
  implementation(Libs.kotlinxCoroutinesReactor)
  implementation(Libs.springBootStarterWebflux)
  implementation(Libs.reactorKotlinExtensions)
  implementation(Libs.nettyTransportNativeKqueue)

  testImplementation(project(Libs.core))
  testImplementation(project(Libs.test))
  testImplementation(project(Libs.springClientWebFlux))
  testImplementation(Libs.undertow)
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
