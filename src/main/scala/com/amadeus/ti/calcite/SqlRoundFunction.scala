/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amadeus.ti.calcite

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.`type`.OperandTypes;
import org.apache.calcite.sql.`type`.ReturnTypes;
import org.apache.calcite.sql.validate.SqlMonotonicity;
import org.apache.calcite.sql.fun.SqlMonotonicUnaryFunction;

import com.google.common.base.Preconditions;

/**
 * Definition of the "ROUND" SQL functions.
 */
class SqlRoundFunction extends SqlMonotonicUnaryFunction(
  "ROUND",
  SqlKind.OTHER_FUNCTION,
  ReturnTypes.ARG0_OR_EXACT_NO_SCALE,
  null,
  OperandTypes.or(OperandTypes.NUMERIC_OR_INTERVAL,
    OperandTypes.sequence(
      "'ROUND(<DATE> TO <TIME_UNIT>)'\n"
      + "'ROUND(<TIME> TO <TIME_UNIT>)'\n"
      + "'ROUND(<TIMESTAMP> TO <TIME_UNIT>)'",
      OperandTypes.DATETIME,
      OperandTypes.ANY
    )
  ),
  SqlFunctionCategory.NUMERIC) {
  //~ Methods ----------------------------------------------------------------

}

// End SqlFloorFunction.java
