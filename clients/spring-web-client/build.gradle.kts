//apply(from = "https://raw.githubusercontent.com/arrow-kt/arrow/main/arrow-libs/gradle/publication.gradle")

plugins {
  `maven-publish`
}

dependencies {
  compileOnly(project(Libs.core))
  implementation(Libs.springBootStarterWeb)
  implementation(Libs.kotlinxCoroutines)
  testImplementation(project(Libs.core))
  testImplementation(project(Libs.test))
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = "com.fortysevendegrees.arrow-endpoint"
      artifactId = "client-spring-web"
      version = "0.0.1"

      from(components["java"])
    }
  }
}
