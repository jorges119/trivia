package com.onesockpirates.trivia.routes

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
import com.onesockpirates.trivia.services.PullerService
import com.onesockpirates.common.models.OTDBQuestion
import com.onesockpirates.common.Extensions.StringExtensions.RichString
import com.onesockpirates.common.models.QuestionPrompt
import com.onesockpirates.common.models.Answer
import com.onesockpirates.trivia.endpoints.TriviaEndpoints

object TriviaController:
  val routes: Routes[Question.Repository & PullerService, Response] =
    Routes(
      TriviaEndpoints.questions.implement((amount: Int) =>
        for
          _ <- ZIO.when(amount < 1 || amount > 50)(
            ZIO.fail("Number of questions out of range (0-50)")
          )
          questions <- ZIO.serviceWithZIO[PullerService](
            _.getquestions(amount)
              .mapError(e => e.getMessage())
          )
          processed <- ZIO
            .succeed(questions.map(Question.fromOTDB))
            .debug(">>> READY")
          _ <- ZIO
            .collectAll(
              processed.map(q =>
                for
                  dbItem <- Question.Repository
                    .findById(q.question.hash)
                    .orElseFail("NA")
                  _ <- ZIO.when(!dbItem.isDefined)(
                    Question.Repository
                      .save(q)
                      .orElseFail("NA")
                  )
                yield ()
              )
            )
        yield (processed.map(q =>
          QuestionPrompt(q.hash, q.question, q.answers)
        ))
      ),
      TriviaEndpoints.checkanswers.implement((answers: List[Answer]) =>
        ZIO.collectAll(
          answers.map(a =>
            for
              question <- Question.Repository
                .findById(a.hash)
                .orElseFail("NA")
              _ <- ZIO.when(!question.isDefined)(ZIO.fail("Unknown answer"))
              result <- Question.Repository
                .update(
                  question.get
                    .copy(isCorrect = a.guess == question.get.correctAnswer)
                )
                .orElseFail("NA")
              _ <- ZIO.when(!result.isDefined)(
                ZIO.fail("Could not update state")
              )
            yield (result.get)
          )
        )
      )
    )
