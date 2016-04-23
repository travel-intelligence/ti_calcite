package com.amadeus.ti.calcite

import scala.collection.JavaConversions._

import argonaut._, Argonaut._

import org.apache.calcite.sql.`type`.SqlTypeName
import org.apache.calcite.rel.`type`.{RelDataType,RelDataTypeFactory}
import org.apache.calcite.schema.impl.{AbstractTable,AbstractSchema}

object SchemaParser {

  case class Column(name: String, dataType: SqlTypeName)

  object Column {
    implicit def ColumnCodec: CodecJson[Column] =
      CodecJson(
        (c: Column) =>
          ("name" := c.name) ->:
          ("type" := c.dataType.getName) ->:
          jEmptyObject,
        j => for {
          name <- (j --\ "name").as[String]
          dataType <- (j --\ "type").as[String].map {
            case s => Option(SqlTypeName.get(s)).getOrElse(SqlTypeName.NULL)
          }
        } yield Column(name, dataType)
      )
  }

  case class Table(name: String, columns: List[Column]) extends AbstractTable {
    def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
      val (names, types) = columns.map {
          case c => (c.name, typeFactory.createSqlType(c.dataType))
        }.unzip
      typeFactory.createStructType(types, names)
    }

  }

  object Table {
    implicit def TableCodec: CodecJson[Table] =
      casecodec2(Table.apply, Table.unapply)("name", "columns")
  }

  case class Schema(name: String, tables: List[Table]) extends AbstractSchema {
    override def getTableMap: java.util.Map[String,org.apache.calcite.schema.Table] = {
      mapAsJavaMap(tables.map( t => (t.name, t) ).toMap)
    }
  }

  object Schema {
    implicit def SchemaCodec: CodecJson[Schema] =
      casecodec2(Schema.apply, Schema.unapply)("name", "tables")
  }

  def parse(json: String): Option[Schema] = json.decodeOption[Schema]
}
