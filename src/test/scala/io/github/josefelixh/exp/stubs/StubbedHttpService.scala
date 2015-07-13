package io.github.josefelixh.exp.stubs

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.{Suite, BeforeAndAfterAll}

object StubbedHttpService {
  val Port = 8080
  val Host = "localhost"
  val server = new WireMockServer(wireMockConfig().port(Port))
}

trait StubbedHttpService extends BeforeAndAfterAll { this: Suite =>
  import StubbedHttpService._

  override def beforeAll() = {
    server start()
    WireMock configureFor(Host, Port)
    super.beforeAll
  }

  override def afterAll() = {
    server stop()
    super.afterAll
  }
}
