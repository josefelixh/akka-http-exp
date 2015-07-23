package io.github.josefelixh.exp.http

import akka.http.scaladsl.model.MediaType.Encoding
import akka.http.scaladsl.model.{HttpCharsets, MediaType}

object versioning {

  val `application/vnd.service.v1+json` =
    MediaType.custom("application/vnd.service.v1+json", Encoding.Fixed(HttpCharsets.`UTF-8`))

}
