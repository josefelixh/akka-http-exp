package io.github.josefelixh.exp

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.datastax.driver.core.Cluster
import io.github.josefelixh.exp.healthcheck._
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.pattern.ask

import scala.concurrent.{ExecutionContextExecutor, Future}

case class ServiceStatus(`http-service`: String, `db`: String)

trait Protocols extends DefaultJsonProtocol {
  implicit val statusFormat = jsonFormat2(ServiceStatus)
}

trait Service extends Protocols {

  implicit val dispatcher: ExecutionContextExecutor

  val dbActor: ActorRef
  val httpServiceActor: ActorRef

  import scala.concurrent.duration._
  import scala.language.postfixOps
  implicit val timeout = Timeout(1 second)

  val routes: Route = {
    pathPrefix("status") {
      complete { serviceStatus }
    }
  }

  private def serviceStatus = for {
    dbStatus <- (dbActor ? Status).mapTo[State]
    httpServiceStatus <- (httpServiceActor ? Status).mapTo[State]
  } yield ServiceStatus(httpServiceStatus, dbStatus)

}
