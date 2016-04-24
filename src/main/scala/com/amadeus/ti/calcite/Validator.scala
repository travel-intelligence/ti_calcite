package com.amadeus.ti.calcite

import scala.util.{Try,Failure,Success}

import org.apache.calcite.jdbc.CalciteSchema
import org.apache.calcite.jdbc.JavaTypeFactoryImpl

import org.apache.calcite.sql.SqlDialect
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.pretty.SqlPrettyWriter
import org.apache.calcite.sql.validate.SqlValidatorUtil
import org.apache.calcite.sql.fun.SqlStdOperatorTable

import org.apache.calcite.prepare.CalciteCatalogReader

object Validator {
  def apply(request: ValidationRequest): ValidationResponse = {

    val parser = SqlParser.create(request.sql)

    val types = new JavaTypeFactoryImpl()

    var rootSchema = CalciteSchema.createRootSchema(false)

    request.schemas.foreach { case s => rootSchema.add(s.name, s) }

    val validator = SqlValidatorUtil.newValidator(
      SqlStdOperatorTable.instance(),
      new CalciteCatalogReader(
        rootSchema,
        false,
        new java.util.Vector[String](),
        types),
      types)

    val result = for {
      rawAst <- Try(parser.parseQuery)
      validAst <- Try(validator.validate(rawAst))
    } yield validAst

    result match {
      case Failure(hint) =>
        ValidationResponse(false, "", Option(hint.getMessage).getOrElse(""))

      case Success(ast) => {
        val pp = new SqlPrettyWriter(SqlDialect.DUMMY)
        val fmt = pp.format(ast)

        ValidationResponse(true, fmt, "")        
      }
    }
  }
}
