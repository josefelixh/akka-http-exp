package io.github.josefelixh.exp.stubs

import java.net.BindException

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.FatalStartupException
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.{BeforeAndAfter, Suite, BeforeAndAfterAll}
import org.slf4j.LoggerFactory

import scala.annotation.tailrec

object StubbedHttpService {
  val log = LoggerFactory.getLogger("StubbedHttpService")
  private val Ports = Stream.from(8080, 1).iterator
  private def nextPort = synchronized {
    val n = Ports.next()
    log.info(s"Issuing port $n")
    n
  }
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
    log.info(s"Resetting WireMockServer to default mappings $Host:$Port")
    server.resetToDefaultMappings()
    super.before(fun)
  }

  override def afterAll() = {
    log.info(s"Stopping WireMockServer $Host:$Port")
    server stop()
    super.afterAll
  }

  private def startServer: Unit = {
    def startServer(port: Int): Unit = {
      try {
        log.info(s"Starting WireMockServer $Host:$port")
        server = new WireMockServer(wireMockConfig().bindAddress(Host).port(port))
        server start()
        log.info(s"WireMockServer $Host:$port started")
        _port = port
      } catch {
        case fse: FatalStartupException => fse.getCause.getCause match {
          case be: BindException => startServer(nextPort)
          case _ => throw fse
        }
      }
    }
    startServer(nextPort)
  }

}
