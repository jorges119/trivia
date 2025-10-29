package users

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.raquo.laminar.api.L.{given, *}

import org.scalajs.dom
import com.onesockpirates.common.models.QuestionPrompt

@js.native @JSImport("/javascript.svg", JSImport.Default)
val javascriptLogo: String = js.native

@main
def LiveChart(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )

object Main:

  def appElement(): Element =
    div(
      a(
        href := "https://vitejs.dev",
        target := "_blank",
        img(src := "/vite.svg", className := "logo", alt := "Vite logo")
      ),
      a(
        href := "https://developer.mozilla.org/en-US/docs/Web/JavaScript",
        target := "_blank",
        img(
          src := javascriptLogo,
          className := "logo vanilla",
          alt := "JavaScript logo"
        )
      ),
      h1("Hello Laminar!"),
      div(className := "card", counterButton()),
      niceDiv(),
      p(className := "read-the-docs", "Click on the Vite logo to learn more")
    )
  end appElement

  def counterButton(): Element =
    val counter = Var(0)
    button(
      tpe := "button",
      "count is ",
      "this ",
      child.text <-- counter,
      onClick --> { event => counter.update(c => c + 1) },
      onMouseEnter --> { event => counter.update(_ + 1) },
      onMouseLeave --> { event => counter.update(_ - 1) }
    )
  end counterButton

  def niceDiv(): Element =
    val question: Var[QuestionPrompt] = Var(
      QuestionPrompt("id1", "Paco", List(""))
    )
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
      p(text <-- question.signal.map(_.hash)),
      p(text <-- signal, color := "red", fontWeight := "bold"),
      input(
        placeholder := "Write something here...",
        value <-- signal,
        onKeyUp.mapToValue --> state.writer
      ),
      button("You can click me", boxShadow := "2px 2px 4px black")
    )
  end niceDiv

end Main
