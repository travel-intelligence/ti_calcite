package com.amadeus.ti.calcite

import argonaut._, Argonaut._

import org.apache.calcite.sql.`type`.SqlTypeName

object SchemaParser {

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

  implicit def TableCodec: CodecJson[Table] =
    casecodec2(Table.apply, Table.unapply)("name", "columns")

  implicit def SchemaCodec: CodecJson[Schema] =
    casecodec2(Schema.apply, Schema.unapply)("name", "tables")
}
