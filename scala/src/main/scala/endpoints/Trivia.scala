package com.onesockpirates.trivia.endpoints

import zio.http.{
  handler,
  Method,
  Request,
  Response,
  Route,
  Routes,
  Server,
  Status
}
import zio.http.codec.PathCodec.{empty, literal}
import zio.json.*
import zio.ZIO
import com.onesockpirates.common.models.Question
import zio.http.endpoint.Endpoint
import zio.http.Method.GET
import zio.http.codec.Doc
import zio.http.codec.PathCodec.path
import zio.http.codec.HttpCodec
import com.onesockpirates.common.models.Answer
import zio.http.Method.POST
import com.onesockpirates.common.models.QuestionPrompt

object TriviaEndpoints:

  val questions =
    Endpoint(GET / "questions" ?? Doc.p("Route for querying questions"))
      .query(
        HttpCodec.query[Int]("amount") ?? Doc.p(
          "Number of questions to retrieve"
        )
      )
      .out[List[QuestionPrompt]]
      .outError[String](Status.BadRequest)

  val checkanswers =
    Endpoint(
      POST / "checkanswers" ?? Doc.p("Route for checking questions' answers")
    )
      .in[List[Answer]]
      .out[List[Question]](Status.Created)
      .outError[String](Status.NotFound)
