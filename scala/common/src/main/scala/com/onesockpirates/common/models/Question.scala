package com.onesockpirates.common.models

import zio.{Task, ZIO}
import zio.json._
import zio.schema.DeriveSchema
import zio.schema.Schema
import zio.schema.annotation.fieldName
import zio.schema.annotation.transientField
import zio.schema.annotation.optionalField
import zio.schema.annotation.fieldDefaultValue

import com.onesockpirates.common.Extensions.StringExtensions.*
import zio.http.codec.HttpCodec
import zio.http.codec.HttpCodecType
import zio.http.codec.ContentCodec
import zio.http.MediaType
import scala.annotation.meta.field
import zio.json.ast.Json
import com.onesockpirates.common.models.OTDBResponse.jsonCodec

trait QuestionBase:
  val hash: String
  val question: String
  val answers: List[String]

case class QuestionPrompt(
    hash: String,
    question: String,
    answers: List[String]
) extends QuestionBase

object QuestionPrompt {
  implicit val schema: Schema[QuestionPrompt] = DeriveSchema.gen[QuestionPrompt]
}

case class Question(
    hash: String,
    @transientField
    @fieldDefaultValue("")
    question: String,
    @transientField
    @fieldDefaultValue("")
    answers: List[String],
    @transientField
    @fieldDefaultValue("")
    correctAnswer: String,
    numCorrect: Int,
    numGuessed: Int,
    isCorrect: Boolean
) extends QuestionBase

object Question {
  implicit val schema: Schema[Question] = DeriveSchema.gen[Question]

  def fromOTDB(question: OTDBQuestion) =
    Question(
      question.question.hash,
      question.question,
      scala.util.Random.shuffle(
        question.correctAnswer :: question.incorrectAnswers
      ),
      question.correctAnswer,
      0,
      0,
      false
    )

  trait Repository {
    def save(question: Question): Task[Question]
    def findById(hash: String): Task[Option[Question]]
    def update(question: Question): Task[Option[Question]]
  }

  object Repository {

    def save(
        question: Question
    ): ZIO[Question.Repository, Throwable, Question] =
      ZIO.serviceWithZIO[Question.Repository](_.save(question))

    def findById(
        hash: String
    ): ZIO[Question.Repository, Throwable, Option[Question]] =
      ZIO.serviceWithZIO[Question.Repository](_.findById(hash))

    def update(
        question: Question
    ): ZIO[Question.Repository, Throwable, Option[Question]] =
      ZIO.serviceWithZIO[Question.Repository](_.update(question))

  }

}
