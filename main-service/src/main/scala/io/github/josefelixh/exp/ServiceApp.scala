package io.github.josefelixh.exp

import java.io.EOFException

import akka.actor.{ActorSystem, ActorRef}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.datastax.driver.core.Cluster
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scala.io.StdIn

object ServiceApp extends Service with App {

  val log = LoggerFactory.getLogger("main-service")

  implicit val actorSystem = ActorSystem("akka-http-exp")
  override implicit val dispatcher = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()

  log.debug("Building cassandra's driver cluster")
  val cluster = Cluster.builder().addContactPoint("localhost").withPort(9042).build()
  log.debug("Cassandra's driver cluster built")

  log.debug("Creating db-actor")
  override lazy val dbActor: ActorRef = actorSystem.actorOf(DbActor.props(cluster), "db-actor")
  log.debug("db-actor created")
  override lazy val httpServiceActor: ActorRef = actorSystem.actorOf(HttpServiceActor.props("localhost", 8080))

  log.debug("Binding port")
  Http().bindAndHandle(routes, "localhost", 9000)
  log.debug("Port bind")

  sys addShutdownHook {
    log.debug("main-service stopping")
    cluster.close()
    actorSystem.shutdown()
    actorSystem.awaitTermination()
  }

  `Ctrl+D`

  sys exit()

  private def `Ctrl+D`: Unit = {
    @tailrec
    def rec(stop: Boolean): Unit = stop match {
      case true => ()
      case false => rec {
        try { StdIn.readChar(); false } catch {
          case e: StringIndexOutOfBoundsException => false
          case e: EOFException => true
        }
      }
    }
    rec(false)
  }
}
