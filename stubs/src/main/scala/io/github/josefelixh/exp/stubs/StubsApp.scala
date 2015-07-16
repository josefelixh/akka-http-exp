package io.github.josefelixh.exp.stubs

import java.io.EOFException

import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner
import org.scassandra.server.{ScassandraConfig, ServerStubRunner}

import scala.annotation.tailrec
import scala.io.StdIn

object StubsApp extends App {
  val wiremockServer = runWiremock(args)
  val scassandraServer = runScassandra(args)

  `Ctrl+D`

  stopWireMock
  stopScassandra

  private def runWiremock(args: Array[String]) = {
    val wiremockServerRunner = new WireMockServerRunner
    wiremockServerRunner.run(args: _*)
    wiremockServerRunner
  }

  private def stopWireMock = wiremockServer.stop()

  private def runScassandra(args: Array[String]) = {
    val binaryListenAddress = ScassandraConfig.binaryListenAddress
    val binaryPortNumber = ScassandraConfig.binaryPort
    val adminListenAddress = ScassandraConfig.adminListenAddress
    val adminPortNumber = ScassandraConfig.adminPort

    val config =
      s"""
        |binaryListenAddress:     ${ScassandraConfig.binaryListenAddress}
        |binaryPortNumber:        ${ScassandraConfig.binaryPort}
        |adminListenAddress:      ${ScassandraConfig.adminListenAddress}
        |adminPortNumber:         ${ScassandraConfig.adminPort}
      """.stripMargin


    println(s"Using binary port to $binaryPortNumber and admin port to $adminPortNumber")

    val scassandraServer = new ServerStubRunner(binaryListenAddress, binaryPortNumber, adminListenAddress, adminPortNumber)
    scassandraServer.start()
    scassandraServer.awaitStartup()
    println(scassandraBanner)
    println(config)
    scassandraServer
  }

  private def stopScassandra = {
    scassandraServer.shutdown()
    scassandraServer.awaitTermination()
  }

  private def `Ctrl+D`: Unit = {
    @tailrec
    def rec(stop: Boolean): Unit = stop match {
      case true => ()
      case false => rec {
        try { StdIn.readChar(); false } catch {
          case e: StringIndexOutOfBoundsException => false
          case e: EOFException => true
        }
      }
    }
    rec(false)
  }

  private def scassandraBanner =
    """
      |  /$$$$$$                                                                   /$$
      | /$$__  $$                                                                 | $$
      || $$  \__/  /$$$$$$$  /$$$$$$   /$$$$$$$ /$$$$$$$  /$$$$$$  /$$$$$$$   /$$$$$$$  /$$$$$$  /$$$$$$
      ||  $$$$$$  /$$_____/ |____  $$ /$$_____//$$_____/ |____  $$| $$__  $$ /$$__  $$ /$$__  $$|____  $$
      | \____  $$| $$        /$$$$$$$|  $$$$$$|  $$$$$$   /$$$$$$$| $$  \ $$| $$  | $$| $$  \__/ /$$$$$$$
      | /$$  \ $$| $$       /$$__  $$ \____  $$\____  $$ /$$__  $$| $$  | $$| $$  | $$| $$      /$$__  $$
      ||  $$$$$$/|  $$$$$$$|  $$$$$$$ /$$$$$$$//$$$$$$$/|  $$$$$$$| $$  | $$|  $$$$$$$| $$     |  $$$$$$$
      | \______/  \_______/ \_______/|_______/|_______/  \_______/|__/  |__/ \_______/|__/      \_______/
      |""".stripMargin
}
