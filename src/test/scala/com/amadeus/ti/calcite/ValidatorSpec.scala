package com.amadeus.ti.calcite

import org.specs2.mutable.Specification

object ValidatorSpec extends Specification {

  val hr = Schema("HR", List(Table("EMPS", List())))

  "validating a correct SQL query" >> {

    val sql = "select 1 from HR.EMPS"

    val request = ValidationRequest(sql, List(hr))

    val response = Validator(request)

    response.valid must beTrue
    response.sql must_== """SELECT 1
FROM `HR`.`EMPS`"""
  }

  "validating an incorrect SQL query" >> {

    val request = ValidationRequest("select 1 from HR.DEPTS", List(hr))

    val response = Validator(request)

    response.valid must beFalse
    response.hint must contain("Table 'HR.DEPTS' not found")
  }
}
