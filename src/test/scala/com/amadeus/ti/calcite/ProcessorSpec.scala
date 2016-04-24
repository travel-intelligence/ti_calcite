package com.amadeus.ti.calcite

import org.specs2.mutable.Specification

class ProcessorSpec extends Specification {

  "processing a validation request for a correct query" >> {
    val request = """
{
  "validation": {
    "sql": "select EMPNAME from HR.EMPS",
    "schemas": [{
        "name": "HR",
        "tables": [{
          "name": "EMPS",
          "columns": [
            { "name": "EMPID", "type": "INTEGER" },
            { "name": "EMPNAME", "type": "VARCHAR" },
            { "name": "DEPTNO", "type": "INTEGER" }
          ]
        }]
    }]
  }
}
"""

    val response = Processor(request)

    response must_== """{
  "validation" : {
    "valid" : true,
    "sql" : "SELECT `EMPNAME`\nFROM `HR`.`EMPS`",
    "hint" : ""
  }
}"""
  }

  "processing an invalid request" >> {
    val request = "{}"

    val response = Processor(request)

    response must_== """{
  "error" : "unable to process"
}"""
  }

  "processing an invalid request" >> {
    val request = """{ "unknown": 42 }"""

    val response = Processor(request)

    response must_== """{
  "error" : "unable to process"
}"""
  }

  "processing an invalid request" >> {
    val request = """{ "validation": {} }"""

    val response = Processor(request)

    response must_== """{
  "error" : "unable to process"
}"""
  }
}
