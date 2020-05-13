package com.amadeus.ti.calcite

import scala.util.{Failure, Success, Try}
import org.apache.calcite.jdbc.CalciteSchema
import org.apache.calcite.avatica.util.{Casing, Quoting}
import org.apache.calcite.sql.{SqlKind, SqlOperatorTable}
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.fun.SqlStdOperatorTable
import org.apache.calcite.schema.SchemaPlus
import org.apache.calcite.sql.pretty.SqlPrettyWriter
import org.apache.calcite.tools.{Frameworks, Planner}

object Validator {

  // A SQL dialect for Cloudera Impala
  lazy val impalaDialect = ImpalaDialect()

  lazy val quoting = Quoting.DOUBLE_QUOTE
  lazy val unquotedCasing = Casing.TO_UPPER
  lazy val quotedCasing = Casing.UNCHANGED

  def apply(request: ValidationRequest): ValidationResponse = {
    // new
    val planner = getPlanner(request.schemas)
    val writer = new SqlPrettyWriter(impalaDialect)
    
    val result = for {
      parsed <- Try(planner.parse(request.sql)) if parsed.isA(SqlKind.QUERY)
      validated <- Try(planner.validate(parsed))
      impala <- Try(writer.format(validated))
    } yield impala
    
    result match {
      case Failure(hint) =>
        ValidationResponse(false, "", Option(hint.getMessage).getOrElse(""))

      case Success(query) => {
        ValidationResponse(true, query, "")
      }
    }
  }
  
  def getPlanner(schemas: List[Schema]): Planner = {
    val config = Frameworks.newConfigBuilder()
      .parserConfig(getSqlParserConfig)
      .defaultSchema(getRootSchema(schemas))
      .operatorTable(getOperatorTable)
      .build()
    
    Frameworks.getPlanner(config)
  }
  
  def getRootSchema(schemas: List[Schema]): SchemaPlus = {
    // Create a root schema without metadata and without caching
    val rootSchema = CalciteSchema.createRootSchema(false, false)
    schemas.foreach { case s => rootSchema.add(s.name, s) }

    rootSchema.plus()
  }
  
  def getSqlParserConfig: SqlParser.Config = {
    SqlParser.configBuilder()
      .setQuoting(quoting)
      .setUnquotedCasing(unquotedCasing)
      .setQuotedCasing(quotedCasing)
      .build()
  }
  
  def getOperatorTable: SqlOperatorTable = {
    val opTable = SqlStdOperatorTable.instance()
    opTable.register(new SqlRoundFunction)
    opTable.register(new SqlConcatFunction)
    opTable.register(new SqlSubstrFunction)
    opTable.register(new SqlGroupConcatFunction)
    
    opTable
  }
}
