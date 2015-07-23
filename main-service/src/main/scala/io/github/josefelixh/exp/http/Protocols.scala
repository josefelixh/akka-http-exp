package io.github.josefelixh.exp.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling._
import io.github.josefelixh.exp.http.versioning._
import spray.json._

object protocols {

  case class ServiceStatus(`http-service`: String, `db`: String)

  trait Protocols extends DefaultJsonProtocol with SprayJsonSupport {

    import scala.language.implicitConversions

    implicit def v1JsonMarshallerConverter[T](writer: RootJsonWriter[T])(implicit printer: JsonPrinter = PrettyPrinter): ToEntityMarshaller[T] =
      v1JsonMarshaller[T](writer, printer)

    implicit def v1JsonMarshaller[T](implicit writer: RootJsonWriter[T], printer: JsonPrinter = PrettyPrinter): ToEntityMarshaller[T] =
      v1JsValueMarshaller compose writer.write

    implicit def v1JsValueMarshaller(implicit printer: JsonPrinter = PrettyPrinter): ToEntityMarshaller[JsValue] =
      Marshaller.StringMarshaller.wrap(`application/vnd.service.v1+json`)(printer)

    implicit val statusFormat = jsonFormat2(ServiceStatus)

  }

}