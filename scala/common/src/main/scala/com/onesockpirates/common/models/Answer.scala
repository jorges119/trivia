package com.onesockpirates.common.models

import zio.schema.DeriveSchema
import zio.schema.Schema

case class Answer(
    hash: String,
    guess: String
)

object Answer {
  implicit val schema: Schema[Answer] = DeriveSchema.gen[Answer]
}
