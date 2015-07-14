package io.github.josefelixh.exp.stubs

import com.github.tomakehurst.wiremock.client.{MappingBuilder, WireMock}

object WireMockClient {
  def apply(host: String = "localhost", port: Int = 8080): WireMockClient = new WireMockClient(host, port)
}

class WireMockClient(val host: String, val port: Int) {
  val instance = new WireMock(host, port)

  def givenThat(mappingBuilder: MappingBuilder) = instance.register(mappingBuilder)
  def stubFor(mappingBuilder: MappingBuilder) = givenThat(mappingBuilder)
}
