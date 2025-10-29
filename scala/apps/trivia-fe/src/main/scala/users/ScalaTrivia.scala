package com.onesockpirates.trivia.frontend

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.raquo.laminar.api.L.{given, *}

import org.scalajs.dom
import com.onesockpirates.common.models.QuestionPrompt
import org.scalajs.dom.Request
import com.onesockpirates.common.models.Question
import com.onesockpirates.common.models.Answer

@js.native @JSImport("/javascript.svg", JSImport.Default)
val javascriptLogo: String = js.native

@main
def ScalaTrivia(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )

object Main:

  def appElement(): Element =
    val amount = Var(10)
    val running = Var(false)
    val finished = Var(false)
    div(
      child <-- running.signal.map(
        if (_)
          div(
            h1("Scala Trivia"),
            p("Good luck"),
            Questionaire()
          )
        else
          div(
            h1("Scala Trivia"),
            p("Choose the number of questions"),
            slider(amount),
            label(amount.signal),
            startButton(running)
          )
      )
    )
  end appElement

  def Questionaire(): Element =
    val responseS: EventStream[Status[Request, List[QuestionPrompt]]] =
      EventStream
        .fromValue(
          Request("http://localhost:8081/questions?amount=10"),
          emitOnce = true
        )
        .flatMapWithStatus { request =>
          FetchStream.get(request.url).map { json =>
            // TODO Jorge: move codec to the model's companion object
            zio.schema.codec.JsonCodec
              .jsonDecoder(QuestionPrompt.listSchema)
              .decodeJson(json)
              .fold(
                error => throw new Exception(s"JSON decode error: $error"),
                question => question
              )
          }
        }

    div(
      child <-- responseS.splitStatus(
        (resolved, _) => Questions(resolved.output),
        (pending, _) => div(img(src("spinner.gif")), "Loading ...")
      )
    )

  def Questions(questions: List[QuestionPrompt]): Element =
    val answers: Var[List[Answer]] = Var(questions.map(q => Answer(q.hash, "")))
    div(
      div(
        questions.map(q => Question(q, answers))
      ),
      button(
        "Check Answers",
        backgroundColor := "lightgray",
        color := "black"
      )
    )

  def Question(question: QuestionPrompt, answers: Var[List[Answer]]): Element =
    div(
      p(question.question),
      question.answers.map(a =>
        p(
          a,
          backgroundColor <-- answers.signal.map(as =>
            if (as.exists(sa => question.hash == sa.hash && a == sa.guess))
              "red"
            else "blue"
          ),
          onClick --> { event =>
            answers.update(ori =>
              ori.filter(l => l.hash != question.hash) :+ Answer(
                question.hash,
                a
              )
            )
          }
        )
      )
    )

  def label(signal: StrictSignal[Int]): Element =
    p(
      p(text <-- signal, color := "white", fontWeight := "bold")
    )

  def slider(amount: Var[Int]): Element =
    input(
      tpe := "range",
      value <-- amount.signal.map(v => s"${v}"),
      idAttr := "amount",
      minAttr := "0",
      maxAttr := "50",
      onInput.mapToValue --> amount.updater[String] { (data, newValue) =>
        newValue.toInt
      }
    )

  def startButton(running: Var[Boolean]): Element =
    button(
      tpe := "button",
      "Start",
      onClick --> { event => running.update(_ => true) }
    )
  end startButton

  def niceDiv(): Element =
    // val question: Var[List[QuestionPrompt]] = Var(
    //   List()
    // )
    val state = Var("Initial")
    val signal = state.signal
    val style = List(
      backgroundColor := "grey",
      borderRadius := "10px",
      padding := "40px",
      display := "flex",
      flexDirection := "column"
    )
    div(
      style,
      // p(text <-- question.signal.map(s => s.hash)),
      p(text <-- signal, color := "blue", fontWeight := "bold"),
      input(
        placeholder := "Write something here...",
        value <-- signal,
        onKeyUp.mapToValue --> state.writer
      )
    )
  end niceDiv

end Main
