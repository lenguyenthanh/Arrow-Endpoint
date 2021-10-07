import java.nio.file.Paths

//apply(from = "https://raw.githubusercontent.com/arrow-kt/arrow/main/arrow-libs/gradle/publication.gradle")

plugins {
  `maven-publish`
}

dependencies {
  // Needed for Uri MatchNamedGroupCollection, ties us to JDK8
  // TODO https://app.clickup.com/t/kt7qd2
  implementation(kotlin("stdlib-jdk8"))
  implementation(Libs.kotlinxCoroutines)
}



publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = "com.fortysevendegrees.arrow-endpoint"
      artifactId = "core"
      version = "0.0.1"

      from(components["java"])
    }
  }
}
