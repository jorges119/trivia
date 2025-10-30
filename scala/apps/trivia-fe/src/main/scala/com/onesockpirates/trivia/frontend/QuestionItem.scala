package com.onesockpirates.trivia.frontend

import com.raquo.laminar.api.L.{given, *}
import com.onesockpirates.common.models.{QuestionPrompt, Answer, Question}

object QuestionItem:

  def component(
      question: QuestionPrompt,
      answers: Var[List[Answer]],
      results: EventStream[List[Question]]
  ): Element =
    val qText = p(
      margin := "0",
      padding := "0",
      fontSize := "1.15rem",
      fontWeight := "600",
      color := "#1e293b",
      lineHeight := "1.6"
    )
    qText.ref.innerHTML = question.question

    div(
      qText,
      child <-- results.map(rs =>
        rs.find(q => q.hash == question.hash).match {
          case None => p("Question not found")
          case Some(question) =>
            p(
              s"${question.numCorrect} out of ${question.numGuessed} players got this question right",
              margin := "8px 0 0 0",
              fontSize := "0.9rem",
              color := "#64748b",
              fontWeight := "500"
            )
        }
      ),
      div(
        question.answers.map(answer => answerOption(question, answer, answers)),
        display := "flex",
        flexDirection := "column",
        gap := "12px"
      ),
      margin := "10px",
      backgroundColor := "#28382d",
      overflow := "hidden",
      borderRadius := "25px",
      borderWidth := "2px",
      borderStyle := "solid",
      borderColor <-- results.map(qs =>
        if (qs.isEmpty)
          "black"
        else if (qs.exists(q => q.hash == question.hash && q.isCorrect))
          "green"
        else "red"
      ),
      backgroundColor := "rgba(255,255,255,0.95)",
      borderRadius := "16px",
      padding := "24px",
      boxShadow := "0 4px 12px rgba(0,0,0,0.1)",
      transition := "all 0.3s ease",
      border := "3px solid transparent",
      borderColor <-- results.map(qs =>
        if (qs.isEmpty)
          "transparent"
        else if (qs.exists(q => q.hash == question.hash && q.isCorrect))
          "#10b981"
        else "#ef4444"
      )
    )

  private def answerOption(
      question: QuestionPrompt,
      answer: String,
      answers: Var[List[Answer]]
  ): Element =
    val hovered: Var[Boolean] = Var(false)
    val aText = p(margin := "0")
    aText.ref.innerHTML = answer

    div(
      aText,
      backgroundColor <-- answers.signal
        .combineWith(hovered.signal)
        .map((as, hov) =>
          if (
            hov || (
              as
                .exists(sa => question.hash == sa.hash && answer == sa.guess)
            )
          )
            "#606160"
          else "#a1b3a6"
        ),
      onClick --> { event =>
        answers.update(ori =>
          ori.filter(l => l.hash != question.hash) :+ Answer(
            question.hash,
            answer
          )
        )
      },
      display := "flex",
      alignItems := "center",
      padding := "16px 20px",
      borderRadius := "12px",
      cursor := "pointer",
      transition := "all 0.2s ease",
      border := "2px solid transparent",
      backgroundColor <-- answers.signal
        .combineWith(hovered.signal)
        .map { (as, hov) =>
          val isSelected =
            as.exists(sa => question.hash == sa.hash && answer == sa.guess)
          if (isSelected)
            "#667eea"
          else if (hov)
            "#f1f5f9"
          else "#f8fafc"
        },
      color <-- answers.signal.map { as =>
        val isSelected =
          as.exists(sa => question.hash == sa.hash && answer == sa.guess)
        if (isSelected) "white" else "#334155"
      },
      fontWeight <-- answers.signal.map { as =>
        val isSelected =
          as.exists(sa => question.hash == sa.hash && answer == sa.guess)
        if (isSelected) "600" else "500"
      },
      transform <-- hovered.signal.map {
        if (_) "translateX(4px)" else "translateX(0)"
      },
      boxShadow <-- answers.signal
        .combineWith(hovered.signal)
        .map { (as, hov) =>
          val isSelected =
            as.exists(sa => question.hash == sa.hash && answer == sa.guess)
          if (isSelected)
            "0 4px 12px rgba(102, 126, 234, 0.4)"
          else if (hov)
            "0 2px 8px rgba(0,0,0,0.08)"
          else "0 1px 3px rgba(0,0,0,0.05)"
        },
      onMouseEnter.mapTo(true) --> hovered.writer,
      onMouseLeave.mapTo(false) --> hovered.writer
    )

end QuestionItem
