package com.onesockpirates.frontend

import com.raquo.laminar.api.L.{given, *}
import org.scalajs.dom
import org.scalajs.dom.Request
import com.onesockpirates.common.models.{QuestionPrompt, Answer, Question}

object Questionnaire:

  def component(running: Var[Boolean]): Element =
    val responseS: EventStream[Status[Request, List[QuestionPrompt]]] =
      EventStream
        .fromValue(
          Request("http://localhost:8081/questions?amount=10"),
          emitOnce = true
        )
        .flatMapWithStatus { request =>
          FetchStream.get(request.url).map { json =>
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
        (resolved, _) => Questions.component(resolved.output, running),
        (pending, _) => div(img(src("spinner.gif")))
      )
    )

end Questionnaire
