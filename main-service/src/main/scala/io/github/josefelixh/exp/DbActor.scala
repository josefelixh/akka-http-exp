package io.github.josefelixh.exp

import akka.actor.{ActorRef, Actor, Props}
import com.datastax.driver.core.{ResultSet, Cluster}
import com.google.common.util.concurrent.{
  Futures => GFutures,
  FutureCallback => GFutureCallback
}
import io.github.josefelixh.exp.healthcheck._

object DbActor {
  def props(cluster: Cluster) = Props(new DbActor(cluster))
}

class DbActor(cluster: Cluster) extends Actor {

  val session = cluster.connect()

  override def receive: Receive = {
    case Status =>  dbStatus(sender())
  }

  private def dbStatus(sender: ActorRef) = {
    GFutures.addCallback(
      session.executeAsync("select cluster_name from system.local;"),
      new GFutureCallback[ResultSet] {
        override def onFailure(t: Throwable) = sender ! Unhealthy
        override def onSuccess(result: ResultSet) = sender ! Healthy
      },
      context.dispatcher
    )
  }
}
