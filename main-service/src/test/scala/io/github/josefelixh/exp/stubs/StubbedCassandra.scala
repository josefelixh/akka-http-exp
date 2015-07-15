package io.github.josefelixh.exp.stubs

import com.datastax.driver.core.Cluster
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.scassandra.{Scassandra, ScassandraFactory}
import org.slf4j.LoggerFactory

import scala.collection.immutable.Stack

private object StubbedCassandra {
  val log = LoggerFactory.getLogger("StubbedCassandra")
  val BinaryPorts = Stream.from(58042, 2).iterator
  def nextBinaryPort = synchronized {
    val n = BinaryPorts.next()
    log.info(s"Issuing binary port $n")
    n
  }
  val AdminPorts = Stream.from(58043, 2).iterator
  def nextAdminPort = synchronized {
    val n = AdminPorts.next()
    log.info(s"Issuing admin port $n")
    n
  }
}

trait StubbedCassandra extends BeforeAndAfterAll { this: Suite =>
  import StubbedCassandra._

  private var _scassandra: Scassandra = _
  def scassandra = _scassandra
  private var _cluster: Cluster = _
  def cluster = _cluster

  val contactPoint = "localhost"
  val binaryPort = nextBinaryPort
  val adminPort = nextAdminPort

  override def beforeAll(): Unit = {
    log.info(s"Creating Scassandra at $contactPoint binary:$binaryPort admin:$adminPort")
    _scassandra = ScassandraFactory.createServer(binaryPort, adminPort)
    _scassandra.start()
    _cluster = Cluster.builder()
      .addContactPoint(contactPoint)
      .withPort(scassandra.getBinaryPort)
      .build()

    super.beforeAll()
  }

  override def afterAll(): Unit = {
    log.info(s"Stopping Scassandra at $contactPoint binary:$binaryPort admin:$adminPort")
    scassandra.stop()
    _cluster.close()

    super.afterAll()
  }

}
