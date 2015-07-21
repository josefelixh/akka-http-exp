package io.github.josefelixh.exp

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.MediaType.Encoding
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import io.github.josefelixh.exp.healthcheck._
import spray.json._
import akka.pattern.ask
import scala.concurrent.ExecutionContextExecutor

case class ServiceStatus(`http-service`: String, `db`: String)

trait Protocols extends DefaultJsonProtocol {
  import scala.language.implicitConversions
  implicit def sprayJsonMarshallerConverter[T](writer: RootJsonWriter[T])(implicit printer: JsonPrinter = PrettyPrinter): ToEntityMarshaller[T] =
    sprayJsonMarshaller[T](writer, printer)
  implicit def sprayJsonMarshaller[T](implicit writer: RootJsonWriter[T], printer: JsonPrinter = PrettyPrinter): ToEntityMarshaller[T] =
    sprayJsValueMarshaller compose writer.write
  implicit def sprayJsValueMarshaller(implicit printer: JsonPrinter = PrettyPrinter): ToEntityMarshaller[JsValue] =
    Marshaller.StringMarshaller.wrap(ContentType(MediaType.custom("application/vnd.service.v1+json", Encoding.Fixed(HttpCharsets.`UTF-8`))))(printer)

  implicit val statusFormat = jsonFormat2(ServiceStatus)
}

trait Service extends Protocols {
  implicit val dispatcher: ExecutionContextExecutor

  val dbActor: ActorRef
  val httpServiceActor: ActorRef

  import scala.concurrent.duration._
  import scala.language.postfixOps
  implicit val timeout = Timeout(10 seconds)

  val routes = {
    pathPrefix("status") {
      complete { serviceStatus }
    }
  }

  private def serviceStatus = for {
    dbStatus <- (dbActor ? Status).mapTo[State]
    httpServiceStatus <- (httpServiceActor ? Status).mapTo[State]
  } yield ServiceStatus(httpServiceStatus, dbStatus)

}