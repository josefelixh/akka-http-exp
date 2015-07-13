package io.github.josefelixh.exp

import akka.stream.ActorMaterializer
import akka.actor.{ActorRef, Props, Actor}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.{StatusCodes, HttpRequest}
import akka.stream.scaladsl.{Sink, Source}
import io.github.josefelixh.exp.healthcheck._

object HttpServiceActor {

  def props(host: String, port: Int) = Props(new HttpServiceActor(host, port))

}

class HttpServiceActor(host: String, port: Int) extends Actor {
  private implicit val system = context.system
  private implicit val dispatcher = context.dispatcher
  private implicit val materializer = ActorMaterializer()

  override def receive: Receive = {
    case Status => serviceStatus(sender())
  }

  private lazy val serviceConnectionFlow = Http().outgoingConnection(host, port)

  private def serviceRequest(request: HttpRequest) =
    Source.single(request).via(serviceConnectionFlow).runWith(Sink.head)

  private def serviceStatus(sender: ActorRef) = serviceRequest(RequestBuilding.Get("/service/status")).map {
    _.status match {
      case StatusCodes.NoContent => sender ! Healthy
      case _ => sender ! Unhealthy
    }
  }
}
