package com.amadeus.ti.calcite

import scalaz._, Scalaz._
import argonaut._, Argonaut._

import org.specs2.mutable.Specification

import ValidationRequestParser._

object ValidationRequestParserSpec extends Specification {

  "loading a validation request from a JSON document" >> {

    val json = """
{
  "sql": "select 1",
  "schemas": []
}
"""
    val request = Parse.decodeOption[ValidationRequest](json)

    request must_== Some(ValidationRequest("select 1", List()))
  }
}
