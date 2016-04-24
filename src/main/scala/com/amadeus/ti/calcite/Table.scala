package com.amadeus.ti.calcite

import scala.collection.JavaConversions._

import org.apache.calcite.rel.`type`.{RelDataType,RelDataTypeFactory}
import org.apache.calcite.schema.impl.AbstractTable

case class Table(name: String, columns: List[Column]) extends AbstractTable {
  def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
    val (names, types) = columns.map {
        case c => (c.name, typeFactory.createSqlType(c.dataType))
      }.unzip
    typeFactory.createStructType(types, names)
  }
}
