package io.github.josefelixh.exp

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import io.github.josefelixh.exp.healthcheck._
import io.github.josefelixh.exp.stubs.StubbedCassandra
import org.scalatest.{GivenWhenThen, FlatSpecLike}
import org.scassandra.http.client.PrimingRequest
import org.scassandra.http.client.PrimingRequest.Result._
import DbActor._
import scala.collection.JavaConversions._

class DbActorSpec extends TestKit(ActorSystem("test-system"))
  with ImplicitSender
  with FlatSpecLike
  with StubbedCassandra
  with GivenWhenThen {

  import StubbedCassandra._

  trait DbActorTest {
    val dbActor = system.actorOf(DbActor.props(cluster))
  }

  "DbActor" should "report unhealthy when cluster is unavailable" in new DbActorTest {
    Given("Cassandra is unavailable")
    scassandra.primingClient().prime(
      PrimingRequest.queryBuilder()
        .withQuery("select cluster_name from system.local;")
        .withResult(unavailable)
        .build()
    )

    When("the Status is requested")
    dbActor ! Status

    Then("unhealthy is reported")
    expectMsg(Unhealthy)
  }

  it should "report healthy when cluster is up" in new DbActorTest {
    Given("Cassandra is available")
    scassandra.primingClient().prime(
      PrimingRequest.queryBuilder()
        .withQuery("select cluster_name from system.local;")
        .withRows(Map("cluster_name" -> "Test Cluster"))
        .build()
    )

    When("the Status is requested")
    dbActor ! Status

    Then("healthy is reported")
    expectMsg(Healthy)
  }

}
