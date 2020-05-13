package com.amadeus.ti.calcite

import org.apache.calcite.sql.`type`.SqlTypeName

import org.specs2.mutable.Specification

object ValidatorSpec extends Specification {

  val hr = Schema("HR", List(
             Table("EMPS", List(
               Column("EMPID", SqlTypeName.INTEGER),
               Column("EMPNAME", SqlTypeName.VARCHAR),
               Column("DEPTNO", SqlTypeName.INTEGER),
               Column("SIZE", SqlTypeName.DOUBLE),
               Column("WEIGHT", SqlTypeName.DECIMAL),
               Column("STARTDATE", SqlTypeName.TIMESTAMP),
               Column("ENDDATE", SqlTypeName.TIMESTAMP))),
             Table("DEPTS", List(
               Column("DEPTNO", SqlTypeName.INTEGER)))
             ))

  def statement(sql: String) = {
    val response = Validator(ValidationRequest(sql, List(hr)))
    // println("SQL statement: " + sql + " - Validated: " + response.sql + " - Hint: " + response.hint)
    response
  }

  def expectNonQueryError(sql: String) = {
    val response = statement(sql)
    response.valid must beFalse
    response.hint must contain("Predicate does not hold")
  }

  def expectValid(sql: String) = statement(sql).valid must beTrue

  def expectNormalized(sql: String, fmt: String) = {
    val response = statement(sql)
    response.valid must beTrue
    response.sql must_== fmt
  }

  "validating and normalizing a correct query" >> {
    expectNormalized(
      "select EMPID from HR.EMPS order by EMPID limit 10",
      """SELECT `EMPS`.`EMPID`
        |FROM `HR`.`EMPS` `EMPS`
        |ORDER BY `EMPID`
        |LIMIT 10""".stripMargin)
  }

  "rejecting a query on an unknown table" >> {
    val response = statement("select DEPTNO from HR.NOT_A_TABLE")
    response.valid must beFalse
    response.hint must contain("Object 'NOT_A_TABLE' not found within 'HR'")
  }

  "validating a LIKE function" >> {
    expectValid("select EMPNAME from HR.EMPS where EMPNAME like '%JON%'")
  }

  "validating an inner join query" >> {
    expectValid("select e.EMPID, e.EMPNAME, e.DEPTNO from HR.EMPS e inner join HR.DEPTS d on (e.DEPTNO = d.DEPTNO)")
  }

  "validating a full outer join query" >> {
    expectValid("select e.EMPID, e.EMPNAME, e.DEPTNO from HR.EMPS e full outer join HR.DEPTS d on (e.DEPTNO = d.DEPTNO)")
  }

  "validating a union query" >> {
    expectValid("select EMPID from HR.EMPS union select DEPTNO from HR.DEPTS")
  }

  "validating a query using WITH" >> {
    expectValid("with sub_table as (select EMPID from HR.EMPS) select * from sub_table")
  }

  "validating an aggregated query" >> {
    expectValid("select EMPNAME, sum(DEPTNO) as TOTAL from HR.EMPS group by EMPNAME order by TOTAL desc limit 10")
  }

  "validating a HAVING clause" >> {
    expectValid("select EMPNAME, sum(DEPTNO) from HR.EMPS group by EMPNAME having sum(DEPTNO) > 0")
  }

  "validating an AVG aggregation function" >> {
    expectValid("select EMPNAME, avg(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a COUNT aggregation function" >> {
    expectValid("select EMPNAME, count(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a FIRST_VALUE aggregation function" >> {
    expectValid("select EMPNAME, first_value(EMPNAME) over(order by EMPNAME) from HR.EMPS group by EMPNAME")
  }

  "validating a LAST_VALUE aggregation function" >> {
    expectValid("select EMPNAME, last_value(EMPNAME) over(order by EMPNAME) from HR.EMPS group by EMPNAME")
  }

  "validating a MAX aggregation function" >> {
    expectValid("select EMPNAME, max(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a MIN aggregation function" >> {
    expectValid("select EMPNAME, min(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a SUM aggregation function" >> {
    expectValid("select EMPNAME, sum(DEPTNO) from HR.EMPS group by EMPNAME")
  }

  "validating a GROUP_CONCAT aggregation function" >> {
    expectValid("select GROUP_CONCAT(EMPNAME) from HR.EMPS")
  }

  "validating a GROUP_CONCAT aggregation function with separator" >> {
    expectValid("select GROUP_CONCAT(EMPNAME, '---') from HR.EMPS")
  }

  "validating a ROUND(double) scalar function" >> {
    expectValid("select EMPNAME, round(SIZE) from HR.EMPS")
  }

  "validating a ROUND(double, int) scalar function" >> {
    expectValid("select EMPNAME, round(SIZE, 5) from HR.EMPS")
  }

  "validating a ROUND(decimal, int) scalar function" >> {
    expectValid("select EMPNAME, round(WEIGHT, 5) from HR.EMPS")
  }

  "validating a SUBSTR function with no end" >> {
    expectValid("select substr(EMPNAME, 4) from HR.EMPS")
  }

  "validating and normalizing a SUBSTR function with no end" >> {
    expectNormalized(
      "select substr(EMPNAME, 4) from HR.EMPS",
      """SELECT SUBSTR(`EMPS`.`EMPNAME`, 4)
        |FROM `HR`.`EMPS` `EMPS`""".stripMargin)
  }

  "validating a SUBSTR function with an end" >> {
    expectValid("select substr(EMPNAME, 4, 8) from HR.EMPS")
  }

  "validating and normalizing a SUBSTR function with an end" >> {
    expectNormalized(
      "select substr(EMPNAME, 4, 8) from HR.EMPS",
      """SELECT SUBSTR(`EMPS`.`EMPNAME`, 4, 8)
        |FROM `HR`.`EMPS` `EMPS`""".stripMargin)
  }

  "validating a CONCAT function without column names" >> {
    expectValid("select concat('a', 'b') from HR.EMPS")
  }

  "validating a CONCAT function with column names" >> {
    expectValid("select concat('a', EMPNAME, 'b') from HR.EMPS")
  }

  "rejecting a set statement" >> {
    expectNonQueryError("SET x = 1")
  }

  "rejecting a reset statement" >> {
    expectNonQueryError("RESET ALL")
  }

  "rejecting an explain statement" >> {
    expectNonQueryError("EXPLAIN PLAN FOR SELECT 1 FROM HR.EMPS")
  }

  "rejecting a describe statement" >> {
    expectNonQueryError("DESCRIBE TABLE HR.EMPS")
  }

  "rejecting an insert statement" >> {
    expectNonQueryError("INSERT INTO HR.EMPS (EMPID) SELECT EMPID FROM HR.EMPS")
  }

  "rejecting an update statement" >> {
    expectNonQueryError("UPDATE HR.EMPS SET EMPID = 0")
  }

  "rejecting a merge statement" >> {
    expectNonQueryError("MERGE INTO HR.EMPS USING HR.DEPTS ON (true) WHEN MATCHED THEN UPDATE SET EMPID = 0")
  }

  "rejecting a delete statement" >> {
    expectNonQueryError("DELETE FROM HR.EMPS")
  }
  
  "transform Calcite dialect to Impala" >> {
    expectNormalized(
      "SELECT TIMESTAMPDIFF(DAY, STARTDATE, ENDDATE) from HR.EMPS",
      """SELECT DATEDIFF(`EMPS`.`ENDDATE`, `EMPS`.`STARTDATE`)
        |FROM `HR`.`EMPS` `EMPS`""".stripMargin)
  }
}
