package io.github.josefelixh.exp

import akka.http.scaladsl.model.ContentType
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import io.github.josefelixh.exp.healthcheck.Healthy
import io.github.josefelixh.exp.http.versioning._
import io.github.josefelixh.exp.stubs.{StubbedCassandra, StubbedHttpService}
import org.json4s.JsonAST.JObject
import org.scalatest.{FlatSpec, Matchers}
import org.scassandra.http.client.PrimingRequest
import org.json4s._
import org.json4s.jackson.JsonMethods._

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

    Get("/status").withHeaders(Accept(`application/vnd.service.v1+json`)) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe ContentType(`application/vnd.service.v1+json`)
      responseAs[JValue] shouldBe JObject(
        "http-service" -> Healthy,
        "db" -> Healthy
      )
    }
  }

  it should "respond to post creating new Products" in new ServiceTest {
    Post("/products")
      .withHeaders(Accept(`application/vnd.service.v1+json`))
      .withEntity(ContentType(`application/vnd.service.v1+json`), compact(
        JObject( "name" -> JString("product1"))
      )) ~>
    routes ~> check {
      status shouldBe NoContent
    }
  }

}
