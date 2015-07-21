package io.github.josefelixh.exp

import akka.http.scaladsl.model.MediaType.Encoding
import akka.http.scaladsl.model.{HttpCharsets, MediaType, ContentType}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.StatusCodes._
import com.github.tomakehurst.wiremock.client.WireMock.{get, urlEqualTo, aResponse}
import io.github.josefelixh.exp.healthcheck.Healthy
import io.github.josefelixh.exp.stubs.{StubbedHttpService, StubbedCassandra}
import org.scalatest.{Matchers, FlatSpec}
import org.scassandra.http.client.PrimingRequest
import spray.json._
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContextExecutor

class ServiceSpec extends FlatSpec
  with Matchers
  with ScalatestRouteTest
  with StubbedHttpService
  with StubbedCassandra {

  trait ServiceTest extends Service {
    override val dispatcher: ExecutionContextExecutor = executor
    override val dbActor = system.actorOf(DbActor.props(cluster))
    override val httpServiceActor = system.actorOf(HttpServiceActor.props(host, port))
  }

  "Service" should "respond to status request" in new ServiceTest {
    scassandra.primingClient().prime(
      PrimingRequest.queryBuilder()
        .withQuery("select cluster_name from system.local;")
        .withRows(Map("cluster_name" -> "Test Cluster"))
        .build()
    )

    wiremockClient.stubFor(get(urlEqualTo("/service/status")).willReturn(aResponse().withStatus(204)))

    Get("/status").withHeaders(RawHeader("Accept", "application/vnd.service.v1+json")) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe ContentType(MediaType.custom("application/vnd.service.v1+json", Encoding.Fixed(HttpCharsets.`UTF-8`)))
      responseAs[String].parseJson shouldBe JsObject(
        "http-service" -> Healthy,
        "db" -> Healthy
      )
    }
  }

}
