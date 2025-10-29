package com.onesockpirates.trivia.endpoints

import zio.ZIO
import zio.http.{handler, Method, Request, Response, Routes}
import zio.http.endpoint.Endpoint
import zio.http.codec.PathCodec.path
import zio.http.codec.Doc
import zio.http.codec.HttpCodec
import zio.http.RoutePattern
import zio.ZNothing
import zio.http.Status

object HealthcheckEndpoints:

  val ep =
    Endpoint(
      RoutePattern.GET / "healthcheck" ?? Doc.p(
        "Route for checking service is alive"
      )
    )
      .out[String]
      .outError[String](Status.BadRequest)
