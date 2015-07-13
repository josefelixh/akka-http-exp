package io.github.josefelixh.exp

object healthcheck {

  case object Status

  sealed abstract class State
  case object Healthy extends State
  case object Unhealthy extends State

}
