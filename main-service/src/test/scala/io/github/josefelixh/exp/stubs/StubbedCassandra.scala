package io.github.josefelixh.exp.stubs

import com.datastax.driver.core.Cluster.{ builder => clusterBuilder }
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.scassandra.ScassandraFactory
import org.slf4j.LoggerFactory

private object StubbedCassandra {
  val log = LoggerFactory.getLogger("StubbedCassandra")
  val binaryPorts = Stream.from(59042, 2).iterator
  def nextBinaryPort = synchronized { binaryPorts.next() }
  val adminPorts = Stream.from(59043, 2).iterator
  def nextAdminPort = synchronized { adminPorts.next() }
}

trait StubbedCassandra extends BeforeAndAfterAll { this: Suite =>
  import StubbedCassandra._

  val contactPoint = "localhost"
  val binaryPort = nextBinaryPort
  val adminPort = nextAdminPort

  val (scassandra, cluster) = {
    log.info(s"Creating Scassandra at $contactPoint binary:$binaryPort admin:$adminPort")
    val scassandra = ScassandraFactory.createServer(binaryPort, adminPort)
    scassandra.start()
    log.info(s"Scassandra at $contactPoint binary:$binaryPort admin:$adminPort started")
    val builder = clusterBuilder().addContactPoint(contactPoint).withPort(scassandra.getBinaryPort).build()
    (scassandra, builder)
  }

  override def afterAll(): Unit = {
    log.info(s"Stopping Scassandra at $contactPoint binary:$binaryPort admin:$adminPort")
    scassandra.stop()
    log.info(s"Scassandra at $contactPoint binary:$binaryPort admin:$adminPort stopped")
    cluster.close()
    super.afterAll()
  }

}
