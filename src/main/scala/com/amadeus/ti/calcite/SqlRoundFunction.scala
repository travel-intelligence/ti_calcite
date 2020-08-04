package com.amadeus.ti.calcite

import org.apache.calcite.sql.SqlFunctionCategory
import org.apache.calcite.sql.SqlKind
import org.apache.calcite.sql.`type`.OperandTypes
import org.apache.calcite.sql.`type`.ReturnTypes
import org.apache.calcite.sql.`type`.SqlTypeFamily
import org.apache.calcite.sql.fun.SqlMonotonicUnaryFunction

/**
 * Definition of the "ROUND" SQL function.
 */
class SqlRoundFunction extends SqlMonotonicUnaryFunction(
  "ROUND",
  SqlKind.OTHER_FUNCTION,
  ReturnTypes.ARG0_OR_EXACT_NO_SCALE,
  null,
  OperandTypes.or(
    OperandTypes.NUMERIC,
    OperandTypes.sequence(
      "'ROUND(<DOUBLE>, <INTEGER>)'\n" +
        "'ROUND(<DECIMAL>, <INTEGER>)'\n",
      OperandTypes.NUMERIC,
      OperandTypes.family(SqlTypeFamily.INTEGER)
    )
  ),
  SqlFunctionCategory.NUMERIC) {
  //~ Methods ----------------------------------------------------------------

}

// End SqlRoundFunction.java
