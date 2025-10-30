package com.onesockpirates.frontend

import com.raquo.laminar.api.L.{given, *}
import org.scalajs.dom
import com.onesockpirates.common.models.{QuestionPrompt, Answer, Question}

object Questions:

  def component(
      questions: List[QuestionPrompt],
      running: Var[Boolean]
  ): Element =
    val answers: Var[List[Answer]] = Var(questions.map(q => Answer(q.hash, "")))
    val submitBus = new EventBus[List[Answer]]
    val isChecked: Var[Boolean] = Var(false)

    val resultsRequest: EventStream[Status[List[Answer], List[Question]]] =
      submitBus.events.flatMapWithStatus { answers =>
        val jsonBody =
          zio.schema.codec.JsonCodec
            .jsonEncoder(Answer.listSchema)
            .encodeJson(answers)
            .toString
        FetchStream
          .post(
            url = "http://localhost:8081/checkanswers",
            _.body(jsonBody),
            _.headers("content-type" -> "application/json; charset=utf-8")
          )
          .map { json =>
            zio.schema.codec.JsonCodec
              .jsonDecoder(Question.listSchema)
              .decodeJson(json)
              .fold(
                error => throw new Exception(s"JSON decode error: $error"),
                results => results
              )
          }
      }

    val results: EventStream[List[Question]] = resultsRequest.foldStatus(
      resolved = a =>
        dom.console.log(">>>> Ready")
        isChecked.set(true)
        a.output
      ,
      pending = b =>
        dom.console.log(">>>> Loading")
        List[Question]()
    )

    div(
      div(
        questions.map(q => QuestionItem.component(q, answers, results))
      ),
      child <-- results.map(qs =>
        p(
          s"You got ${qs.count(q => q.isCorrect)} correct answers.",
          fontSize := "3rem",
          fontWeight := "800",
          margin := "0"
        )
      ),
      child <-- isChecked.signal.map(f =>
        if (!f)
          button(
            "Check Answers",
            backgroundColor := "white",
            color := "#667eea",
            fontSize := "1.1rem",
            fontWeight := "700",
            padding := "16px 48px",
            border := "none",
            borderRadius := "12px",
            cursor := "pointer",
            boxShadow := "0 4px 12px rgba(0,0,0,0.15)",
            transition := "all 0.2s ease",
            onClick.mapTo(answers.now()) --> submitBus.writer
          )
        else
          button(
            "Start again",
            backgroundColor := "white",
            color := "#667eea",
            fontSize := "1.1rem",
            fontWeight := "700",
            padding := "16px 48px",
            border := "none",
            borderRadius := "12px",
            cursor := "pointer",
            boxShadow := "0 4px 12px rgba(0,0,0,0.15)",
            transition := "all 0.2s ease",
            onClick --> { event => running.update(_ => false) }
          )
      )
    )

end Questions
