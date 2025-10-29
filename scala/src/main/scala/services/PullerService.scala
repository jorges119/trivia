package com.onesockpirates.trivia.services

import zio._
import zio.http._
import zio.http.codec.TextBinaryCodec.fromSchema
import com.onesockpirates.common.models.OTDBResponse
import com.onesockpirates.common.models.OTDBQuestion

trait PullerService {
  def getquestions(amount: Int): ZIO[Any, Throwable, List[OTDBQuestion]]
}

class PullerServiceReal(client: Client, server: String) extends PullerService {
  def getquestions(amount: Int): ZIO[Any, Throwable, List[OTDBQuestion]] =
    ZIO.scoped {
      for {
        json <- client
          .apply(Request.get(s"${server}?amount=${amount}"))
          .flatMap(
            _.body.asString
          )
        questions <- ZIO.succeed(
          OTDBResponse.jsonCodec
            .decodeJson(json)
            .fold(_ => throw Error(""), payload => payload.results)
        )
      } yield (questions)
    }

}

object PullerServiceLive {

  val layer: ZLayer[Client & String, Throwable, PullerService] =
    ZLayer.fromFunction((c: Client, url: String) =>
      new PullerServiceReal(c, url)
    )
}
