package com.onesockpirates.trivia.endpoints

import zio.ZIO
import zio.http.{handler, Method, Request, Response, Routes}
import com.onesockpirates.trivia.services.PullerService

object HealthcheckController:

  val routes: Routes[Any, Response] =
    Routes(
      HealthcheckEndpoints.ep.implement((Unit) => ZIO.succeed("Alive"))
    )
