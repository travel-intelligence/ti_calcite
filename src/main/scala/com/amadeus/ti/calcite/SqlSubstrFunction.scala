package com.amadeus.ti.calcite

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.`type`.OperandTypes;
import org.apache.calcite.sql.`type`.ReturnTypes;
import org.apache.calcite.sql.`type`.SqlTypeFamily;
import org.apache.calcite.sql.`type`.SqlTypeName;
import org.apache.calcite.sql.validate.SqlMonotonicity;
import org.apache.calcite.sql.fun.SqlMonotonicUnaryFunction;

import com.google.common.base.Preconditions;

/**
 * Definition of the "SUBSTR" SQL function.
 */
class SqlSubstrFunction extends SqlMonotonicUnaryFunction(
  "SUBSTR",
  SqlKind.OTHER_FUNCTION,
  ReturnTypes.explicit(SqlTypeName.VARCHAR),
  null,
  OperandTypes.or(
    OperandTypes.sequence(
      "'SUBSTR(<VARCHAR>, <INTEGER>)'\n",
      OperandTypes.STRING,
      OperandTypes.family(SqlTypeFamily.INTEGER)
    ),
    OperandTypes.sequence(
      "'SUBSTR(<VARCHAR>, <INTEGER>, <INTEGER>)'\n",
      OperandTypes.STRING,
      OperandTypes.family(SqlTypeFamily.INTEGER),
      OperandTypes.family(SqlTypeFamily.INTEGER)
    )
  ),
  SqlFunctionCategory.STRING) {
  //~ Methods ----------------------------------------------------------------

}

// End SqlSubstrFunction.java
