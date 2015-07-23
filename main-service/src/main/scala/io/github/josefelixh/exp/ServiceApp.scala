package io.github.josefelixh.exp

import java.io.EOFException

import akka.actor.{ActorSystem, ActorRef}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.datastax.driver.core.Cluster

import scala.annotation.tailrec
import scala.io.StdIn

object ServiceApp extends Service with App {
  implicit val actorSystem = ActorSystem("akka-http-exp")
  override implicit val dispatcher = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()

  val cluster = Cluster.builder().addContactPoint("localhost").withPort(8042).build()

  override lazy val dbActor: ActorRef = actorSystem.actorOf(DbActor.props(cluster))
  override lazy val httpServiceActor: ActorRef = actorSystem.actorOf(HttpServiceActor.props("localhost", 8080))

  Http().bindAndHandle(routes, "localhost", 9000)

  `Ctrl+D`

  cluster.close()
  actorSystem.shutdown()
  actorSystem.awaitTermination()

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
