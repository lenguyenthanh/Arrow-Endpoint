package arrow.endpoint.docs.openapi

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule


class ResponseSerializerTest : StringSpec({


  val json: Json = Json {
//    encodeDefaults = false
//    prettyPrint = true
  }

  "default response" {
    val response = Response("response")
    val str = json.encodeToString(response)
    val decodedResponse = json.decodeFromString<Response>(str)
    decodedResponse shouldBe response
  }

//  "more complicated response" {
//    val response = Response(
//      "response", content = mapOf(
//        "text/plain" to MediaType(
//          schema = Referenced.Other(
//            value = Schema(
//            )
//          )
//        )
//      )
//    )
//    val str = json.encodeToString(response)
//    val decodedResponse = json.decodeFromString<Response>(str)
//    decodedResponse shouldBe response
//  }

  "Referenced.Other" {
    val schema = Referenced.Other(
      Schema(
      )
    ) as Referenced<Schema>
    val str = json.encodeToString(schema)
    println(str)
//    val decodedSchema = json.decodeFromString<Referenced<Schema>>(str)
//    decodedSchema shouldBe schema
  }

//  "MediaType" {
//    val serializer = Referenced.serializer(Schema.serializer())
//    val s = MediaType.serializer()
//    println(s.descriptor)
//    println(serializer.descriptor)
//    val schema = MediaType(
//      schema = Referenced.Other(
//        value = Schema(
//        )
//      )
//    )
//    val str = json.encodeToString(schema)
//
//    println(str)
//    val decodedSchema = json.decodeFromString<Referenced<Schema>>(str)
//    decodedSchema shouldBe schema
//  }

})
