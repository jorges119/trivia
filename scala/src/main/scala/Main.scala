package com.onesockpirates.trivia

import zio.Console.*
import zio.{
  durationInt,
  ConfigProvider,
  Runtime,
  Schedule,
  Task,
  ZIO,
  ZIOAppArgs,
  ZIOAppDefault,
  ZLayer
}
import zio.config.typesafe.*
import zio.http.{
  handler,
  Method,
  Middleware,
  Request,
  Response,
  Route,
  Routes,
  Server,
  Status
}
import zio.http.endpoint.openapi.*
import zio.http.codec.PathCodec.*
import zio.http.Client
import zio.json.*
import com.onesockpirates.trivia.repositories.PersistentQuestionRepository
import com.onesockpirates.trivia.endpoints.*
import com.onesockpirates.trivia.routes.*
import com.onesockpirates.trivia.configuration.TriviaConfiguration
import com.onesockpirates.trivia.services.PullerServiceLive
import zio.http.Middleware.CorsConfig
import zio.http.Header.Origin
import zio.http.Header.AccessControlAllowOrigin
import zio.http.Path

object RESTServer extends ZIOAppDefault {

  val config: CorsConfig =
    CorsConfig(
      allowedOrigin = {
        case origin
            if origin == Origin.parse("http://localhost:5173").toOption.get =>
          Some(AccessControlAllowOrigin.Specific(origin))
        case _ => None
      }
    )

  val openAPI =
    OpenAPIGen.fromEndpoints(
      title = "Trivia API",
      version = "1.0",
      List(
        HealthcheckEndpoints.ep,
        TriviaEndpoints.questions,
        TriviaEndpoints.checkanswers
      )
    )

  val routes =
    (((HealthcheckController.routes ++ TriviaController.routes) ++ SwaggerUI
      .routes("docs" / "openapi", openAPI)))

  val debug_routes = routes @@ Middleware.cors(
    config
  ) @@ Middleware.debug @@ Middleware
    .serveResources(Path.empty)

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(ConfigProvider.fromResourcePath())

  val namespace = "std"

  override val run =
    for
      _ <- printLine(s"Loading configuration: $namespace")
      cfg <- ZIO.config(TriviaConfiguration.config.nested(namespace))
      _ <- printLine(">>> Using " + cfg.openTriviaURL).fork
      _ <- Server
        .serve(debug_routes)
        .provide(
          Client.default,
          ZLayer.fromZIO(ZIO.succeed(cfg.openTriviaURL)),
          PullerServiceLive.layer,
          PersistentQuestionRepository.layer,
          Server.defaultWithPort(cfg.port.getOrElse(8080))
        )
    yield ()

}
