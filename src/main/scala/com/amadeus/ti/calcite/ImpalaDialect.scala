package com.amadeus.ti.calcite

import org.apache.calcite.config.NullCollation
import org.apache.calcite.sql.{SqlCall, SqlDialect, SqlKind, SqlNode, SqlWriter}
import org.apache.calcite.sql.dialect.HiveSqlDialect

class ImpalaDialect private (context: SqlDialect.Context) extends HiveSqlDialect(context) {
  override def unparseCall(writer: SqlWriter, call: SqlCall, leftPrec: Int, rightPrec: Int): Unit = {
    val kind = call.getKind

    kind match {
      case SqlKind.TIMESTAMP_DIFF =>
        val frame = writer.startFunCall("DATEDIFF")
        (1 until call.operandCount()).reverse.foreach { i =>
          call.operand(i).asInstanceOf[SqlNode].unparse(writer, leftPrec, rightPrec)
          if (i > 1) writer.sep(",")
        }
        writer.endFunCall(frame)
      case _ => super.unparseCall(writer, call, leftPrec, rightPrec)
    }
  }
}

object ImpalaDialect {
  
  def apply(): ImpalaDialect = {
    val context = SqlDialect.EMPTY_CONTEXT
      .withDatabaseProduct(SqlDialect.DatabaseProduct.HIVE)
      .withDatabaseProductName("Cloudera Impala")
      .withIdentifierQuoteString("`")
      .withNullCollation(NullCollation.HIGH)
    
    new ImpalaDialect(context)
  }
}
