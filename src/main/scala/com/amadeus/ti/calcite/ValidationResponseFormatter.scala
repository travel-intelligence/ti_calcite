package com.amadeus.ti.calcite

import argonaut._, Argonaut._

object ValidationResponseFormatter {

  implicit def ValidationResponseCodec: CodecJson[ValidationResponse] =
    casecodec3(ValidationResponse.apply, ValidationResponse.unapply)("valid", "sql", "hint")
}
