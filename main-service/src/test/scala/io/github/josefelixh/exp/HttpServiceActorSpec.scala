package io.github.josefelixh.exp

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.github.tomakehurst.wiremock.client.WireMock
import io.github.josefelixh.exp.stubs.StubbedHttpService
import org.scalatest.{FlatSpecLike, GivenWhenThen}
import com.github.tomakehurst.wiremock.client.WireMock.{get, urlEqualTo, aResponse}
import io.github.josefelixh.exp.healthcheck._

class HttpServiceActorSpec extends TestKit(ActorSystem("test-system"))
  with ImplicitSender
  with FlatSpecLike
  with GivenWhenThen
  with StubbedHttpService {

  import wiremockClient._

  trait HttpServiceActorTest {
    val serviceActor = system.actorOf(HttpServiceActor.props(host, port))
  }

  "HttpServiceActor" should "report unhealthy when can't connect to service" in {
    Given("Connection to the service can't be establish")
    val serviceActor = system.actorOf(HttpServiceActor.props("nonexistenthost", 80))

    When("the status is requested")
    serviceActor ! Status

    Then("unhealthy is reported")
    expectMsg(Unhealthy)
  }

  it should "report unhealthy when service is unavailable" in new HttpServiceActorTest {
    Given("The service status is not available")
    stubFor(get(urlEqualTo("/service/status")).willReturn(aResponse().withStatus(503)))

    When("the status is requested")
    serviceActor ! Status

    Then("unhealthy is reported")
    expectMsg(Unhealthy)
  }

  it should "report healthy when service is available" in new HttpServiceActorTest {
    Given("The service status is not available")
    stubFor(get(urlEqualTo("/service/status")).willReturn(aResponse().withStatus(204)))

    When("the status is requested")
    serviceActor ! Status

    Then("healthy is reported")
    expectMsg(Healthy)
  }
}
