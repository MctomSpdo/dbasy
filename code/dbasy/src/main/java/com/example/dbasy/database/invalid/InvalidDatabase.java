package com.example.dbasy.database.invalid;

import com.example.dbasy.database.*;

import java.sql.SQLException;
import java.util.List;

public class InvalidDatabase extends Database {
    public InvalidDatabase() {
        super();
    }

    @Override
    public String toString() {
        return "INVALID";
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public String getUrl(ConnectionDetails details) {
        return null;
    }

    @Override
    public Database getNewInstance() {
        return null;
    }

    @Override
    public DBUI getUI() {
        return null;
    }

    @Override
    public List<Table> getTables() throws SQLException {
        return null;
    }

    @Override
    public Table loadColumns(Table table) throws SQLException {
        return null;
    }

    @Override
    public Table loadTable(Table table, int limit, int offset) throws SQLException {
        return null;
    }

    @Override
    public Result request(String sql) {
        return null;
    }
}
