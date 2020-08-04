package com.amadeus.ti.calcite

import org.apache.calcite.sql.SqlFunctionCategory
import org.apache.calcite.sql.SqlKind
import org.apache.calcite.sql.`type`.OperandTypes
import org.apache.calcite.sql.`type`.ReturnTypes
import org.apache.calcite.sql.`type`.SqlTypeName
import org.apache.calcite.sql.SqlFunction

/**
 * Definition of the "CONCAT" SQL function.
 */
class SqlConcatFunction extends SqlFunction(
  "CONCAT",
  SqlKind.OTHER_FUNCTION,
  ReturnTypes.explicit(SqlTypeName.VARCHAR),
  null,
  OperandTypes.SAME_VARIADIC,
  SqlFunctionCategory.STRING) {
  //~ Methods ----------------------------------------------------------------

}

// End SqlConcatFunction.java
