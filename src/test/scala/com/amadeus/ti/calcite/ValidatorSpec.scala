package com.amadeus.ti.calcite

import org.apache.calcite.sql.`type`.SqlTypeName

import org.specs2.mutable.Specification

object ValidatorSpec extends Specification {

  val hr = Schema("HR", List(
             Table("EMPS", List(
               Column("EMPID", SqlTypeName.INTEGER),
               Column("EMPNAME", SqlTypeName.VARCHAR),
               Column("DEPTNO", SqlTypeName.INTEGER))),
             Table("DEPTS", List(
               Column("DEPTNO", SqlTypeName.INTEGER)))
             ))

  def statement(sql: String) = {
    val response = Validator(ValidationRequest(sql, List(hr)))
    // println(sql + " : " + response.sql + " : " + response.hint)
    response
  }

  def expect_non_query_error(sql: String) = {
    val response = statement(sql)
    response.valid must beFalse
    response.hint must contain("Predicate does not hold")
  }

  def expect_valid(sql: String) = statement(sql).valid must beTrue

  "validating and normalizing a correct query" >> {
    val response = statement("select EMPID from HR.EMPS")
    response.valid must beTrue
    response.sql must_== """SELECT `EMPID`
FROM `HR`.`EMPS`"""
  }

  "rejecting a query on an unknown table" >> {
    val response = statement("select DEPTNO from HR.NOT_A_TABLE")
    response.valid must beFalse
    response.hint must contain("Table 'HR.NOT_A_TABLE' not found")
  }

  "validating a join query" >> {
    expect_valid("select e.EMPID, e.EMPNAME, e.DEPTNO from HR.EMPS e inner join HR.DEPTS d on (e.DEPTNO = d.DEPTNO) where e.EMPNAME like '%JON%'")
  }

  "validating a union query" >> {
    expect_valid("select EMPID from HR.EMPS union select DEPTNO from HR.DEPTS")
  }

  "validating an aggregated query" >> {
    expect_valid("select EMPNAME, sum(DEPTNO) as TOTAL from HR.EMPS group by EMPNAME order by TOTAL desc limit 10")
  }

  "rejecting a set statement" >> {
    expect_non_query_error("SET x = 1")
  }

  "rejecting a reset statement" >> {
    expect_non_query_error("RESET ALL")
  }

  "rejecting an explain statement" >> {
    expect_non_query_error("EXPLAIN PLAN FOR SELECT 1 FROM HR.EMPS")
  }

  "rejecting a describe statement" >> {
    expect_non_query_error("DESCRIBE TABLE HR.EMPS")
  }.pendingUntilFixed("not implemented yet")

  "rejecting an insert statement" >> {
    expect_non_query_error("INSERT INTO HR.EMPS (EMPID) SELECT EMPID FROM HR.EMPS")
  }

  "rejecting an update statement" >> {
    expect_non_query_error("UPDATE HR.EMPS SET EMPID = 0")
  }

  "rejecting a merge statement" >> {
    expect_non_query_error("MERGE INTO HR.EMPS USING HR.DEPTS ON (true) WHEN MATCHED THEN UPDATE SET EMPID = 0")
  }

  "rejecting a delete statement" >> {
    expect_non_query_error("DELETE FROM HR.EMPS")
  }
}
