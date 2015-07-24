package io.github.josefelixh.exp.http

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.ContentType
import akka.http.scaladsl.unmarshalling._
import akka.stream.Materializer
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import io.github.josefelixh.exp.http.versioning._
import org.json4s._
import org.json4s.jackson.Serialization

object protocols {

  case class ServiceStatus(`http-service`: String, `db`: String)

  trait Protocols extends Json4sSupport {

    import scala.language.implicitConversions

    implicit val serialization = Serialization

    implicit val formats = DefaultFormats + FieldSerializer[ServiceStatus]()

    implicit def v1Unmarshaller[A: Manifest](implicit serialization: Serialization, formats: Formats, mat: Materializer): FromEntityUnmarshaller[A] =
      json4sUnmarshaller[A](manifest, serialization, formats, mat).forContentTypes(`application/vnd.service.v1+json`)

    implicit def v1Marshaller[A <: AnyRef](implicit serialization: Serialization, formats: Formats, shouldWritePretty: ShouldWritePretty = ShouldWritePretty.False): ToEntityMarshaller[A] =
      shouldWritePretty match {
        case ShouldWritePretty.False => Marshaller.StringMarshaller.wrap(ContentType(`application/vnd.service.v1+json`))(serialization.write[A])
        case _                       => Marshaller.StringMarshaller.wrap(ContentType(`application/vnd.service.v1+json`))(serialization.writePretty[A])
      }

  }

}