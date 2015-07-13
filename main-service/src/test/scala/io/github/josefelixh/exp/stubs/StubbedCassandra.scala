package io.github.josefelixh.exp.stubs

import com.datastax.driver.core.Cluster
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.scassandra.{Scassandra, ScassandraFactory}

object StubbedCassandra {
  val scassandra = ScassandraFactory.createServer()
  val cluster = Cluster.builder()
    .addContactPoint("localhost")
    .withPort(scassandra.getBinaryPort)
    .build()
}

trait StubbedCassandra extends BeforeAndAfterAll { this: Suite =>
  import StubbedCassandra._

  override protected def beforeAll(): Unit = {
    scassandra.start()
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    scassandra.stop()
    super.afterAll()
  }

}
