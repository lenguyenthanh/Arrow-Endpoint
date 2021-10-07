package arrow.endpoint.spring.server

import arrow.endpoint.EndpointIO
import arrow.endpoint.EndpointInterceptor
import arrow.endpoint.model.Address
import arrow.endpoint.model.Body
import arrow.endpoint.model.CodecFormat
import arrow.endpoint.model.ConnectionInfo
import arrow.endpoint.model.Header
import arrow.endpoint.model.Method
import arrow.endpoint.model.ServerRequest
import arrow.endpoint.model.ServerResponse
import arrow.endpoint.model.Uri
import arrow.endpoint.server.ServerEndpoint
import arrow.endpoint.server.interpreter.RequestBody
import arrow.endpoint.server.interpreter.ServerInterpreter
import kotlinx.coroutines.reactor.mono
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ReactiveHttpOutputMessage
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.remoteAddressOrNull
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import org.springframework.web.reactive.function.server.ServerRequest as SpringServerRequest
import org.springframework.web.reactive.function.server.ServerResponse as SpringServerResponse

public fun <I, E, O> routerFunction(ses: ServerEndpoint<I, E, O>, interceptors: List<EndpointInterceptor> = emptyList()): RouterFunction<SpringServerResponse> =
  routerFunction(listOf(ses), interceptors)

public fun routerFunction(ses: List<ServerEndpoint<*, *, *>>, interceptors: List<EndpointInterceptor> = emptyList()): RouterFunction<SpringServerResponse> =
  router {
    ses.forEach { endpoint -> add(endpoint.toRouterFunction(interceptors)) }
  }

private fun <I, E, O> ServerEndpoint<I, E, O>.toRouterFunction(interceptors: List<EndpointInterceptor> = emptyList()): RouterFunction<SpringServerResponse> =
  RouterFunction { request: SpringServerRequest ->
    val interpreter = ServerInterpreter(
      request.toServerRequest(),
      SpringRequestBody(request),
      interceptors
    )
    mono { interpreter.invoke(this@toRouterFunction) }
      .flatMap { serverResponse: ServerResponse ->
        serverResponse.withBody()
          ?.let { body: Pair<MediaType, BodyInserter<*, in ServerHttpResponse>> ->
            serverResponse.toSpringServerResponse(body)
          } ?: serverResponse.toSpringServerResponse()
      }.map { response: SpringServerResponse -> HandlerFunction { Mono.just(response) } }
  }

private fun SpringServerRequest.toServerRequest(): ServerRequest {
  val uri = Uri(uri())
  requireNotNull(uri) { "Error parsing the URI: $uri" }
  return ServerRequest(
    protocol = "HTTP/1.1",
    connectionInfo = ConnectionInfo(
      localAddress().orElse(null)?.let { Address(it.hostString, it.port) },
      remoteAddressOrNull()?.let { Address(it.hostString, it.port) },
      null
    ),
    method = Method(methodName()),
    uri = uri,
    headers = headers().asHttpHeaders().toSingleValueMap().map { (name, value) -> Header(name, value) },
    pathSegments = uri.path(),
    queryParameters = uri.params()
  )
}

private class SpringRequestBody(
  private val request: SpringServerRequest
) : RequestBody {
  @Suppress("UNCHECKED_CAST")
  override suspend fun <R> toRaw(bodyType: EndpointIO.Body<R, *>): R {
    val body: DataBuffer? = request.awaitBodyOrNull()
    return when (bodyType) {
      is EndpointIO.ByteArrayBody -> toByteArray(body)
      is EndpointIO.ByteBufferBody -> body?.asByteBuffer()
      is EndpointIO.InputStreamBody -> body?.asInputStream()
      is EndpointIO.StringBody -> body?.toString(Charsets.UTF_8)
    } as R
  }

  private fun toByteArray(body: DataBuffer?) =
    body?.asByteBuffer()?.let { buf ->
      if (buf.hasArray()) buf.array()
      else {
        val bytes = ByteArray(buf.remaining())
        buf.get(bytes, 0, bytes.size)
      }
    }
}

private fun ServerResponse.withBody(): Pair<MediaType, BodyInserter<Any?, ReactiveHttpOutputMessage>>? =
  body?.let { Pair(it.contentType(), BodyInserters.fromValue(it.toByteArray())) }

private fun Body.contentType(): MediaType =
  when (format) {
    is CodecFormat.Json -> MediaType.APPLICATION_JSON
    is CodecFormat.TextPlain -> MediaType.TEXT_PLAIN
    is CodecFormat.TextHtml -> MediaType.TEXT_HTML
    is CodecFormat.OctetStream -> MediaType.APPLICATION_OCTET_STREAM
    is CodecFormat.Zip -> MediaType("application", "zip")
    is CodecFormat.XWwwFormUrlencoded -> MediaType.APPLICATION_FORM_URLENCODED
    is CodecFormat.MultipartFormData -> MediaType.MULTIPART_FORM_DATA
    CodecFormat.TextEventStream -> MediaType.TEXT_EVENT_STREAM
    CodecFormat.Xml -> MediaType.APPLICATION_XML
  }

private fun ServerResponse.toSpringServerResponse(
  body: Pair<MediaType, BodyInserter<*, in ServerHttpResponse>>
): Mono<SpringServerResponse> =
  SpringServerResponse.status(HttpStatus.valueOf(code.code))
    .headers { headers.map { (name, value) -> it.add(name, value) } }
    .contentType(body.first)
    .body(body.second)

private fun ServerResponse.toSpringServerResponse(): Mono<SpringServerResponse> =
  SpringServerResponse.status(HttpStatus.valueOf(code.code))
    .headers { headers.map { (name, value) -> it.add(name, value) } }
    .build()
