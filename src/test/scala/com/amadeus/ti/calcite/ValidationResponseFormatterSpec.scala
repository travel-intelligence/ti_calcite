package com.amadeus.ti.calcite

import scalaz._, Scalaz._
import argonaut._, Argonaut._

import org.specs2.mutable.Specification

import ValidationResponseFormatter._

object ValidationResponseFormatterSpec extends Specification {

  "formatting a JSON document from a validation response" >> {

    val response = ValidationResponse(true, "select 1", "a hint")

    val json = response.asJson.spaces2

    json must_== """{
  "valid" : true,
  "sql" : "select 1",
  "hint" : "a hint"
}"""
  }
}
