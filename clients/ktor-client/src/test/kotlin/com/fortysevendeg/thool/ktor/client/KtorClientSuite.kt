package com.fortysevendeg.thool.ktor.client

import arrow.core.Either
import com.fortysevendeg.thool.DecodeResult
import com.fortysevendeg.thool.Endpoint
import com.fortysevendeg.thool.model.StatusCode
import com.fortysevendeg.thool.test.ClientInterpreterSuite
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

class KtorClientSuite : ClientInterpreterSuite() {

  private val client = HttpClient(CIO)

  override suspend fun <I, E, O> requestAndStatusCode(
    endpoint: Endpoint<I, E, O>,
    baseUrl: String,
    input: I
  ): Pair<DecodeResult<Either<E, O>>, StatusCode> {
    val (_, response, result) = client.config { expectSuccess = false }.execute(endpoint, baseUrl, input)
    return Pair(result, StatusCode(response.status.value))
  }
}
