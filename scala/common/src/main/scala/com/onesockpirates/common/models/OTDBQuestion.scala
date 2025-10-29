package com.onesockpirates.common.models

import zio.schema.annotation.fieldName
import zio.schema.Schema
import zio.schema.DeriveSchema
import zio.json.JsonCodec

case class OTDBQuestion(
    @fieldName("type")
    qType: String,
    difficulty: String,
    category: String,
    question: String,
    @fieldName("correct_answer")
    correctAnswer: String,
    @fieldName("incorrect_answers")
    incorrectAnswers: List[String]
)

object OTDBQuestion {
  implicit val schema: Schema[OTDBQuestion] = DeriveSchema.gen[OTDBQuestion]
  implicit val jsonCodec: JsonCodec[OTDBQuestion] =
    zio.schema.codec.JsonCodec.jsonCodec(schema)
}
