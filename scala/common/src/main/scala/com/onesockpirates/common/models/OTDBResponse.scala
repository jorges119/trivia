package com.onesockpirates.common.models

import zio.schema.annotation.fieldName
import zio.schema.Schema
import zio.schema.DeriveSchema
import zio.json.JsonCodec

case class OTDBResponse(
    @fieldName("response_code")
    responseCode: Int,
    results: List[OTDBQuestion]
)

object OTDBResponse {
  implicit val schema: Schema[OTDBResponse] = DeriveSchema.gen[OTDBResponse]
  implicit val jsonCodec: JsonCodec[OTDBResponse] =
    zio.schema.codec.JsonCodec.jsonCodec(schema)
}
