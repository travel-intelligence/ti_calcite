package com.amadeus.ti.calcite

import scala.collection.JavaConversions._

import org.apache.calcite.schema.impl.AbstractSchema

case class Schema(name: String, tables: List[Table]) extends AbstractSchema {
  override def getTableMap: java.util.Map[String,org.apache.calcite.schema.Table] = {
    mapAsJavaMap(tables.map( t => (t.name, t) ).toMap)
  }
}
