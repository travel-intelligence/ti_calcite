package com.amadeus.ti.calcite;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.adapter.java.JavaTypeFactory;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.schema.impl.AbstractTable;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.pretty.SqlPrettyWriter;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql.validate.SqlValidatorWithHints;
import org.apache.calcite.sql.validate.ParameterScope;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.type.SqlTypeName;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeFactory.FieldInfoBuilder;

import org.apache.calcite.prepare.CalciteCatalogReader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlayGround {

  private static class MyTable extends AbstractTable {

    public MyTable() {
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
      FieldInfoBuilder builder = new FieldInfoBuilder(typeFactory);

      builder.add("DEPTNO", SqlTypeName.INTEGER);
      builder.add("EMPID", SqlTypeName.INTEGER);

      return builder.build();
    }

  }

  private static class MySchema extends AbstractSchema {

    private final HashMap<String,Table> tables = new HashMap<String,Table>();

    public MySchema() {
      tables.put("EMPS", new MyTable());
      tables.put("DEPTS", new MyTable());
    }

    @Override
    protected Map<String,Table> getTableMap() {
      return tables;
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println("Hello World.");

    // Class.forName("org.apache.calcite.jdbc.Driver");

    // Properties info = new Properties();
    // info.setProperty("lex", "JAVA");

    // Connection connection = DriverManager.getConnection("jdbc:calcite:", info);

    // CalciteConnection calciteConnection =
    //   connection.unwrap(CalciteConnection.class);


    // ReflectiveSchema.create(calciteConnection,
    //   calciteConnection.getRootSchema(), "hr", new HrSchema());

    String query =
      "select d.deptno, min(e.empid)\n"
      + "from hr.emps as e\n"
      + "join hr.depts as d\n"
      + "  on e.deptno = d.deptno\n"
      + "group by d.deptno\n"
      + "having count(*) > 1";

    SqlParser parser = SqlParser.create(query);
    SqlNode ast = parser.parseQuery();

    JavaTypeFactory types = new JavaTypeFactoryImpl();

    CalciteSchema rootSchema = CalciteSchema.createRootSchema(false);

    rootSchema.add("HR", new MySchema());

    SqlValidatorWithHints val = SqlValidatorUtil.newValidator(
      SqlStdOperatorTable.instance(),
      new CalciteCatalogReader(
        rootSchema,
        false,
        new Vector<String>(),
        types),
      types);

    SqlNode validAst = val.validate(ast);

    SqlPrettyWriter pp = new SqlPrettyWriter(SqlDialect.DUMMY);
    String fmt = pp.format(ast);
    System.out.println(fmt);

    // Statement statement = calciteConnection.createStatement();

    // ResultSet resultSet = statement.executeQuery(
    //   "select d.deptno, min(e.empid)\n"
    //   + "from hr.emps as e\n"
    //   + "join hr.depts as d\n"
    //   + "  on e.deptno = d.deptno\n"
    //   + "group by d.deptno\n"
    //   + "having count(*) > 1");

    // print(resultSet);

    // resultSet.close();
    // statement.close();
    // connection.close();
  }
}
