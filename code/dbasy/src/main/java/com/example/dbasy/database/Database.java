package com.example.dbasy.database;

import com.example.dbasy.Main;
import com.example.dbasy.Resources;
import com.example.dbasy.database.mysql.MySQLDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Database {
    private Connection conn;
    private ConnectionDetails details;
    private boolean active = false;

    //<editor-fold desc="Getter and Setter">
    public Connection getConn() {
        return conn;
    }

    public ConnectionDetails getDetails() {
        return details;
    }

    public void setDetails(ConnectionDetails details) {
        this.details = details;
    }
    //</editor-fold>

    //<editor-fold desc="Connect and close">
    public void connect() throws SQLException {
        Main.RESOURCES.log.info("Starting DB Connection: " + this.details.host + ":" + this.details.port);
        this.active = true;
        this.conn = DriverManager.getConnection(getUrl(this.details), this.details.username, this.details.password);
    }

    public void close() throws SQLException {
        Main.RESOURCES.log.info("Closing DB Connection: " + this.details.host + ":" + this.details.port);
        this.active = false;
        this.conn.close();
    }
    //</editor-fold>


    /**
     * Returns the name of the Database
     * @return DatabaseName
     */
    @Override
    public abstract String toString();

    /**
     * Returns the default Port used by the Database application
     * @return port for connection
     */
    public abstract int getPort();

    /**
     * Gets the Database String (URL) for a given Database
     * @param details ConnectionDetails to base the url of
     * @return URL
     */
    public abstract String getUrl(ConnectionDetails details);

    /**
     * Gets a new instance of the current Database
     * @return new Database instance
     */
    public abstract Database getNewInstance();

    /**
     * Returns the UI Components for the Database
     * @return UI components
     */
    public abstract DBUI getUI();

    public static void addDatabases() {
        Main.RESOURCES.log.debug("Adding Database select options");
        Resources.repoList.add(new MySQLDatabase());
    }
}
