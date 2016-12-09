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
    // println("SQL statement: " + sql + " - Validated: " + response.sql + " - Hint: " + response.hint)
    response
  }

  def expect_non_query_error(sql: String) = {
    val response = statement(sql)
    response.valid must beFalse
    response.hint must contain("Predicate does not hold")
  }

  def expect_valid(sql: String) = statement(sql).valid must beTrue

  def expect_normalized(sql: String, fmt: String) = {
    val response = statement(sql)
    response.valid must beTrue
    response.sql must_== fmt
  }

  "validating and normalizing a correct query" >> {
    expect_normalized(
      "select EMPID from HR.EMPS order by EMPID limit 10",
      """SELECT `EMPID`
FROM `HR`.`EMPS`
ORDER BY `EMPID`
LIMIT 10""")
  }

  "rejecting a query on an unknown table" >> {
    val response = statement("select DEPTNO from HR.NOT_A_TABLE")
    response.valid must beFalse
    response.hint must contain("Table 'HR.NOT_A_TABLE' not found")
  }

  "validating a LIKE function" >> {
    expect_valid("select EMPNAME from HR.EMPS where EMPNAME like '%JON%'")
  }

  "validating an inner join query" >> {
    expect_valid("select e.EMPID, e.EMPNAME, e.DEPTNO from HR.EMPS e inner join HR.DEPTS d on (e.DEPTNO = d.DEPTNO)")
  }

  "validating a full outer join query" >> {
    expect_valid("select e.EMPID, e.EMPNAME, e.DEPTNO from HR.EMPS e full outer join HR.DEPTS d on (e.DEPTNO = d.DEPTNO)")
  }

  "validating a union query" >> {
    expect_valid("select EMPID from HR.EMPS union select DEPTNO from HR.DEPTS")
  }

  "validating a query using WITH" >> {
    expect_valid("with sub_table as (select EMPID from HR.EMPS) select * from sub_table")
  }

  "validating an aggregated query" >> {
    expect_valid("select EMPNAME, sum(DEPTNO) as TOTAL from HR.EMPS group by EMPNAME order by TOTAL desc limit 10")
  }

  "validating a HAVING clause" >> {
    expect_valid("select EMPNAME, sum(DEPTNO) from HR.EMPS group by EMPNAME having sum(DEPTNO) > 0")
  }

  "validating an AVG aggregation function" >> {
    expect_valid("select EMPNAME, avg(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a COUNT aggregation function" >> {
    expect_valid("select EMPNAME, count(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a FIRST_VALUE aggregation function" >> {
    expect_valid("select EMPNAME, first_value(EMPNAME) over(order by EMPNAME) from HR.EMPS group by EMPNAME")
  }

  "validating a LAST_VALUE aggregation function" >> {
    expect_valid("select EMPNAME, last_value(EMPNAME) over(order by EMPNAME) from HR.EMPS group by EMPNAME")
  }

  "validating a MAX aggregation function" >> {
    expect_valid("select EMPNAME, max(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a MIN aggregation function" >> {
    expect_valid("select EMPNAME, min(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a SUM aggregation function" >> {
    expect_valid("select EMPNAME, sum(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a ROUND scalar function" >> {
    expect_valid("select EMPNAME, round(DEPTNO) from HR.EMPS")
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
  }

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
