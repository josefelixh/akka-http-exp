package io.github.josefelixh.exp.stubs

import java.net.BindException

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.FatalStartupException
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.{BeforeAndAfter, Suite, BeforeAndAfterAll}
import org.slf4j.LoggerFactory

object StubbedHttpService {
  private val log = LoggerFactory.getLogger("StubbedHttpService")
  private val ports = Stream.from(8080, 1).iterator
  private def nextPort = synchronized { ports.next() }
}

trait StubbedHttpService extends BeforeAndAfterAll with BeforeAndAfter { this: Suite =>
  import StubbedHttpService._

  val host = "localhost"
  val (server, port, wiremockClient) = {
    val (server, port) = startServer
    (server, port, WireMockClient(host, port))
  } 
  
  override protected def before(fun: => Any): Unit = {
    log.info(s"Resetting WireMockServer to default mappings $host:$port")
    server.resetToDefaultMappings()
    super.before(fun)
  }

  override def afterAll() = {
    log.info(s"Stopping WireMockServer $host:$port")
    server stop()
    super.afterAll
  }

  private def startServer: (WireMockServer, Int) = {
    def startServer(port: Int): (WireMockServer, Int) = {
      try {
        log.info(s"Starting WireMockServer $host:$port")
        val server = new WireMockServer(wireMockConfig().bindAddress(host).port(port))
        server start()
        log.info(s"WireMockServer $host:$port started")
        (server, port)
      } catch {
        case fse: FatalStartupException => fse.getCause.getCause match {
          case be: BindException => {
            log.info(s"Failed to start WireMockServer $host:$port address already in use.")
            startServer(nextPort)
          }
          case _ => throw fse
        }
      }
    }
    startServer(nextPort)
  }

}
