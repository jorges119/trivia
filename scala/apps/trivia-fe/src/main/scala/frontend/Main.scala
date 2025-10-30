package com.onesockpirates.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.raquo.laminar.api.L.{given, *}
import org.scalajs.dom

@main
def ScalaTrivia(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    App.appElement()
  )
