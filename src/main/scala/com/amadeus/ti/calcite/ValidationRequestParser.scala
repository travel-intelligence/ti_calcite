package com.amadeus.ti.calcite

import argonaut._, Argonaut._

import SchemaParser._

object ValidationRequestParser {

  implicit def ValidationRequestCodec: CodecJson[ValidationRequest] =
    casecodec2(ValidationRequest.apply, ValidationRequest.unapply)("sql", "schemas")
}
