package io.github.josefelixh.exp.stubs

import java.net.BindException

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.FatalStartupException
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.{BeforeAndAfter, Suite, BeforeAndAfterAll}

import scala.annotation.tailrec

object StubbedHttpService {
  private val Ports = Stream.from(8080, 1).iterator
}

trait StubbedHttpService extends BeforeAndAfterAll with BeforeAndAfter { this: Suite =>
  import StubbedHttpService._

  val Host = "localhost"

  private var _port: Int = _
  def Port = _port

  private var server: WireMockServer = _

  private var _client: WireMockClient = _
  def wiremockClient = _client

  override def beforeAll() = {
    startServer
    _client = WireMockClient(Host, Port)
    super.beforeAll
  }

  override protected def before(fun: => Any): Unit = {
    println(s"Reset mappings $Host:$Port")
    server.resetMappings()
    super.before(fun)
  }

  override def afterAll() = {
    println(s"Stop server $Host:$Port")
    server stop()
    super.afterAll
  }

  private def startServer: Unit = {
    def startServer(port: Int): Unit = {
      try {
        server = new WireMockServer(wireMockConfig().bindAddress(Host).port(port))
        server start()
        _port = port
      } catch {
        case fse: FatalStartupException => fse.getCause.getCause match {
          case be: BindException => startServer(Ports.next())
          case _ => throw fse
        }
      }
    }
    startServer(Ports.next())
  }

}
