plugins {
  id(Plugins.kotlinSerialization)
}

dependencies {
  api(project(Libs.thoolModel))
  implementation(Libs.kotlinxCoroutines)
  implementation(Libs.arrowCore)
  testImplementation(Libs.kotestRunner)
  testImplementation(Libs.kotestAssertions)
  testImplementation(Libs.kotestProperty)

  testImplementation(Libs.kotlinxSerializationJson)
  testImplementation(project(Libs.ktor))
  testImplementation(Libs.ktorTest)
  testImplementation(Libs.ktorServerNetty)
  testImplementation(Libs.http4kApache)
}
