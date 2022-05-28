package com.mctom.dbasy.db.mysql;

import com.mctom.dbasy.db.ConnectionDetails;
import com.mctom.dbasy.db.Database;

import java.sql.DriverManager;

public class MySQLDatabase extends Database {
    public MySQLDatabase(ConnectionDetails connectionDetails) {
        super(connectionDetails);
    }

    public MySQLDatabase() {
        super();
    }

    @Override
    public Database getNewInstance() {
        return new MySQLDatabase();
    }

    @Override
    public String getDBString(ConnectionDetails details) {
        return "jdbc:mysql://" + details.getHost() + ":" + details.getPort() + "/" + details.getDatabase();
    }

    @Override
    public int getDefaultPort() {
        return 3306;
    }

    @Override
    public ConnectionDetails getDataFromConnectString(String connectString) {
        try {
            String host = connectString.split(":")[2].substring(2);
            String port = connectString.split(":")[3].split("/")[0];
            String[] arr = connectString.split("/");
            String database = arr[3];

            return new ConnectionDetails(host, port, database);
        } catch (Exception e)  {
            return null;
        }
    }

    @Override
    public String toString() {
        return "MySQL";
    }
}
