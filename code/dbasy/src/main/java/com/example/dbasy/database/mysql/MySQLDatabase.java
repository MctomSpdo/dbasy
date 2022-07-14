package com.example.dbasy.database.mysql;

import com.example.dbasy.database.ConnectionDetails;
import com.example.dbasy.database.Database;

public class MySQLDatabase extends Database {

    @Override
    public String toString() {
        return "MySQL";
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
                details.port +
                "/" +
                details.database;
    }

    @Override
    public Database getNewInstance() {
        return new MySQLDatabase();
    }
}
