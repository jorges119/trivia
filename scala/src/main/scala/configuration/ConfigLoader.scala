package com.onesockpirates.trivia.configuration

import zio.Config

final case class TriviaConfiguration(
    port: Option[Int],
    openTriviaURL: String
)

object TriviaConfiguration {

  val config: Config[TriviaConfiguration] =
    (Config.Optional(Config.int("port"))
      ++ Config.string("otrivia_url")).map {
      case (
            port,
            openTriviaURL
          ) =>
        TriviaConfiguration(
          port,
          openTriviaURL
        )
    }

}
