package com.onesockpirates.frontend

import com.raquo.laminar.api.L.{given, *}

object App:

  def appElement(): Element =
    val amount = Var(10)
    val running = Var(false)

    div(
      h1(
        "Scala Trivia",
        fontSize := "3rem",
        fontWeight := "800",
        color := "white",
        textAlign := "center",
        marginBottom := "10px",
        textShadow := "0 4px 6px rgba(0,0,0,0.3)"
      ),
      child <-- running.signal.map(
        if (_)
          div(
            p(
              "Good luck! 🍀",
              fontSize := "1.25rem",
              color := "rgba(255,255,255,0.9)",
              textAlign := "center",
              marginBottom := "30px"
            ),
            Questionnaire.component(running)
          )
        else
          div(
            p(
              "Choose the number of questions",
              fontSize := "1.1rem",
              color := "#fcfcfcff",
              textAlign := "center"
            ),
            Components.slider(amount),
            Components.label(amount.signal),
            Components.startButton(running)
          )
      ),
      minHeight := "100vh",
      minWidth := "100vw",
      background := "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
      display := "flex",
      alignItems := "center",
      justifyContent := "center",
      flexDirection := "column",
      padding := "20px"
    )
  end appElement

end App
