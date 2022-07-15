package com.example.dbasy.database.mysql;

import com.example.dbasy.database.ConnectionDetails;
import com.example.dbasy.database.DBUI;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        var st = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        st.execute("show tables");
        var rs = st.getResultSet();
        var names = contentFromResult(rs)
                        .stream()
                        .map(list -> list.get(0))
                        .toList();

        rs.close();
        st.close();
        return Table.getTables(names, this);
    }

    /**
     * Loads only the headers for a given Table
     * @param table table
     * @return same table with headers
     * @throws SQLException on error
     */
    @Override
    public Table loadHeaders(Table table) throws SQLException {
        var st = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        st.execute("select * from " + table.getName() + " limit 0");
        var rs = st.getResultSet();
        table.setHeaders(headersFromResult(rs));
        rs.close();
        st.close();
        return table;
    }
}
