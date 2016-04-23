package com.amadeus.ti.calcite

import org.apache.calcite.sql.`type`.SqlTypeName

import org.specs2.mutable.Specification

import SchemaParser._

object SchemaParserSpec extends Specification {

  "loading a schema from a JSON document" >> {

    val json = """
{
  "name": "HR",
  "tables": [{
    "name": "EMPS",
    "columns": [
      { "name": "EMPID", "type": "INTEGER" },
      { "name": "EMPNAME", "type": "VARCHAR" },
      { "name": "DEPTNO", "type": "INTEGER" }
    ]
  }]
}
"""
    val maybeASchema = parse(json)

    maybeASchema must_== Some(
        Schema("HR", List(
          Table("EMPS", List(
            Column("EMPID", SqlTypeName.INTEGER),
            Column("EMPNAME", SqlTypeName.VARCHAR),
            Column("DEPTNO", SqlTypeName.INTEGER)))))
      )
  }
}
