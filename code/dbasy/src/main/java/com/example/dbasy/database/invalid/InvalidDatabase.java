package com.example.dbasy.database.invalid;

import com.example.dbasy.database.ConnectionDetails;
import com.example.dbasy.database.DBUI;
import com.example.dbasy.database.Database;

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
}
