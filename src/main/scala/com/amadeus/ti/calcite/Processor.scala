package com.amadeus.ti.calcite

import scalaz._, Scalaz._
import argonaut._, Argonaut._

import ValidationRequestParser._
import ValidationResponseFormatter._

object Processor {

  val relError      = "error"
  val relValidation = "validation"

  def apply(request: String): String = {

    val response: Option[ValidationResponse] = for {
      j <- Parse.parseOption(request).map(_.hcursor)
      f <- j.fields
      v <- f match {
        case relValidation :: _ =>
          (j --\ relValidation).as[ValidationRequest].toOption.map(Validator(_))
        case _ => None
      }
    } yield v

    val json = response match {
      case Some(r) => Json(relValidation := r)
      case _ => Json(relError := "unable to process")
    }

    json.spaces2
  }
}
