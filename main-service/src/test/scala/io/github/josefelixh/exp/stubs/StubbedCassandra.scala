package io.github.josefelixh.exp.stubs

import com.datastax.driver.core.Cluster
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.scassandra.{Scassandra, ScassandraFactory}

import scala.collection.immutable.Stack

object StubbedCassandra {
  private val BinaryPorts = Stream.from(58042, 2).iterator
  private val AdminPorts = Stream.from(58043, 2).iterator
}

trait StubbedCassandra extends BeforeAndAfterAll { this: Suite =>
  import StubbedCassandra._

  private var _scassandra: Scassandra = _
  def scassandra = _scassandra
  private var _cluster: Cluster = _
  def cluster = _cluster

  override def beforeAll(): Unit = {
    _scassandra = ScassandraFactory.createServer(BinaryPorts.next(), AdminPorts.next())
    _scassandra.start()
    _cluster = Cluster.builder()
      .addContactPoint("localhost")
      .withPort(scassandra.getBinaryPort)
      .build()
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    scassandra.stop()
    _cluster.close()

    super.afterAll()
  }

}
