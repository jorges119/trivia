package com.onesockpirates.trivia.routes

import zio.ZIO
import zio.http.{handler, Method, Request, Response, Routes}
import com.onesockpirates.trivia.services.PullerService
import com.onesockpirates.trivia.endpoints.HealthcheckEndpoints

object HealthcheckController:

  val routes: Routes[Any, Response] =
    Routes(
      HealthcheckEndpoints.ep.implement((Unit) => ZIO.succeed("Alive"))
    )
