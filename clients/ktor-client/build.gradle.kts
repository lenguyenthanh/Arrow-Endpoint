//apply(from = "https://raw.githubusercontent.com/arrow-kt/arrow/main/arrow-libs/gradle/publication.gradle")

dependencies {
  compileOnly(project(Libs.core))
  implementation(Libs.ktorClientCore)

  testImplementation(project(Libs.core))
  testImplementation(Libs.ktorClientCio)
  testImplementation(project(Libs.test))
}
