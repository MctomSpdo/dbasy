package com.example.dbasy.database.mysql;

import com.example.dbasy.Main;
import com.example.dbasy.database.*;
import javafx.scene.control.Tab;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLDatabase extends Database {
    private static final MySQLUI userInterface = new MySQLUI();

    @Override
    public String toString() {
        return this.details.name;
    }

    @Override
    public int getPort() {
        return 3306;
    }

    //jdbc:mysql://localhost:3306/db
    @Override
    public String getUrl(ConnectionDetails details) {
        return "jdbc:mysql://" +
                details.host +
                ":" +
                ((details.port == -1) ? "" : details.port) +
                "/" +
                details.database;
    }

    @Override
    public Database getNewInstance() {
        return new MySQLDatabase();
    }

    @Override
    public DBUI getUI() {
        return userInterface;
    }

    @Override
    public List<Table> getTables() throws SQLException {
        var tables = this.conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
        var tableList = new ArrayList<Table>();
        while (tables.next()) {
            String catalog = tables.getString("TABLE_CAT");
            String schema = tables.getString("TABLE_SCHEM");
            String tableName = tables.getString("TABLE_NAME");
            var table = new Table(tableName, this);

            //primary key columns:
            try (ResultSet primaryKeys = this.conn.getMetaData().getPrimaryKeys(catalog, schema, tableName)) {
                while (primaryKeys.next()) {
                    var column = new Column(table, primaryKeys.getString("COLUMN_NAME"));
                    column.keys.add(new Key(column, "", Key.Type.PRIMARY));
                    table.addColumnsIfNotExists(column);
                }
            }
            // similar for exportedKeys


            tableList.add(table);
        }
        return tableList;
    }

    @Override
    public Table loadHeaders(Table table) throws SQLException {
        var st = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        st.execute("select * from " + table.getName() + " limit 0");
        var rs = st.getResultSet();
        columnsFromResult(rs).forEach(table::addColumnsIfNotExists);
        rs.close();
        st.close();
        return table;
    }

    @Override
    public Table loadTable(Table table, int limit, int offset) throws SQLException {
        Main.RESOURCES.log.debug("Loading Table: " + table.getName());
        var pstmt = conn.prepareStatement("select * from "+ table.getName() + " limit ? offset ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pstmt.setInt(1, limit);
        pstmt.setInt(2, offset);

        pstmt.execute();
        var rs = pstmt.getResultSet();

        table.setColumns(columnsFromResult(rs));
        table.setContent(contentFromResult(rs));

        rs.close();
        pstmt.close();

        Main.RESOURCES.log.debug("Loaded Table: " + table.getName());
        return table;
    }

    @Override
    public Result request(String sql) throws SQLException {
        Main.RESOURCES.log.debug("Querying SQL: " + sql);
        var stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        var bool = stmt.execute(sql);
        var rs = stmt.getResultSet();
        Result result;

        if(rs != null) {
            result = new Result(this, rs, sql);
            rs.close();
        } else {
            result = new Result(true);
        }

        stmt.close();
        return result;
    }
}
