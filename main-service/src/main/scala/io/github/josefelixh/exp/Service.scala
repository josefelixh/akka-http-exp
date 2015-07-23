package io.github.josefelixh.exp

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import io.github.josefelixh.exp.healthcheck._
import io.github.josefelixh.exp.http.protocols._
import akka.pattern.ask
import scala.concurrent.ExecutionContextExecutor

trait Service extends Protocols {
  implicit val dispatcher: ExecutionContextExecutor

  val dbActor: ActorRef
  val httpServiceActor: ActorRef

  import scala.concurrent.duration._
  import scala.language.postfixOps
  implicit val timeout = Timeout(10 seconds)

  val routes =
    (get & pathPrefix("status")) { complete { serviceStatus } } ~
    (post & pathPrefix("products")) { complete { HttpResponse(NoContent) } }


  private def serviceStatus = for {
    dbStatus <- (dbActor ? Status).mapTo[State]
    httpServiceStatus <- (httpServiceActor ? Status).mapTo[State]
  } yield ServiceStatus(httpServiceStatus, dbStatus)

}