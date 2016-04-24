package com.amadeus.ti.calcite

import org.apache.calcite.sql.`type`.SqlTypeName

case class Column(name: String, dataType: SqlTypeName)
