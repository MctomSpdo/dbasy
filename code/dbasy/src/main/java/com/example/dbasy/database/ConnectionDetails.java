package com.example.dbasy.database;

public class ConnectionDetails {
    public String name;
    public String host;
    public int port;
    public String database;
    public String username;
    public String password;
    public boolean isValid = true;

    public ConnectionDetails() {
    }

    public ConnectionDetails(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.name = database + "@" + host;
    }

    public ConnectionDetails(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.name = database + "@" + host;
    }

    public ConnectionDetails(boolean isValid) {
        this.isValid = isValid;
    }
}
