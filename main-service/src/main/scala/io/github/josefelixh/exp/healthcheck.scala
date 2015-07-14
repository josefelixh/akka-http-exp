package io.github.josefelixh.exp

import spray.json.JsString

object healthcheck {

  case object Status

  sealed abstract class State
  case object Healthy extends State
  case object Unhealthy extends State

  import scala.language.implicitConversions
  implicit def state2String(state: State): String = state.toString.toLowerCase
  implicit def state2JsString(state: State): JsString = JsString(state)

}
