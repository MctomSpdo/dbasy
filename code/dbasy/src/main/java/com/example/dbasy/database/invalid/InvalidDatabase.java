package com.example.dbasy.database.invalid;

import com.example.dbasy.database.ConnectionDetails;
import com.example.dbasy.database.DBUI;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.Table;

import java.sql.SQLException;
import java.util.List;

public class InvalidDatabase extends Database {
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
}
