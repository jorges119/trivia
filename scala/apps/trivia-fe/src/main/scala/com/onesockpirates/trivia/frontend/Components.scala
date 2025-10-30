package com.onesockpirates.trivia.frontend

import com.raquo.laminar.api.L.{given, *}

object Components:

  def label(signal: StrictSignal[Int]): Element =
    div(
      display := "flex",
      justifyContent := "center",
      alignItems := "center",
      marginTop := "20px",
      marginBottom := "30px",
      div(
        fontSize := "3.5rem",
        fontWeight := "800",
        child.text <-- signal
      )
    )

  def slider(amount: Var[Int]): Element =
    div(
      position := "relative",
      marginBottom := "10px",
      input(
        tpe := "range",
        value <-- amount.signal.map(v => s"${v}"),
        idAttr := "amount",
        minAttr := "0",
        maxAttr := "50",
        width := "100%",
        height := "8px",
        borderRadius := "4px",
        outline := "none",
        cursor := "pointer",
        onInput.mapToValue --> amount.updater[String] { (data, newValue) =>
          newValue.toInt
        }
      )
    )
  def startButton(running: Var[Boolean]): Element =
    val hovered = Var(false)

    button(
      tpe := "button",
      "Start Quiz",
      width := "100%",
      padding := "18px",
      fontSize := "1.2rem",
      fontWeight := "700",
      color := "white",
      border := "none",
      borderRadius := "12px",
      cursor := "pointer",
      transition := "all 0.3s ease",
      background := "linear-gradient(135deg, #717378ff 0%, #764ba2 100%)",
      boxShadow <-- hovered.signal.map {
        if (_) "0 8px 24px rgba(232, 233, 240, 0.4)"
        else "0 4px 12px rgba(95, 95, 98, 0.3)"
      },
      transform <-- hovered.signal.map {
        if (_) "translateY(-2px)" else "translateY(0)"
      },
      onClick --> { _ => running.update(_ => true) },
      onMouseEnter.mapTo(true) --> hovered.writer,
      onMouseLeave.mapTo(false) --> hovered.writer
    )

end Components
