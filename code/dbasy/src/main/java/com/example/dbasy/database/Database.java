package com.example.dbasy.database;

import com.example.dbasy.Main;
import com.example.dbasy.Resources;
import com.example.dbasy.database.mysql.MySQLDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Database {
    protected Connection conn;
    protected ConnectionDetails details;
    protected boolean active = false;

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

    public abstract List<Table> getTables() throws SQLException;

    public static void addDatabases() {
        Main.RESOURCES.log.debug("Adding Database select options");
        Resources.repoList.add(new MySQLDatabase());
    }

    /**
     * Gets the headers from a given ResultSet
     * @param rs ResultSet
     * @return headers
     * @throws SQLException on error
     */
    public static List<String> headersFromResult(ResultSet rs) throws SQLException {
        var headers = new ArrayList<String>();
        var metaData = rs.getMetaData();
        int columns = metaData.getColumnCount();

        for(int i = 1; i < columns + 1; i++) {
            headers.add(metaData.getColumnName(i));
        }
        return headers;
    }

    public static List<List<String>> contentFromResult(ResultSet rs) throws SQLException {
        try {
            rs.last();
        } catch (SQLException noResult) {
            rs.close();
            throw new IllegalArgumentException("ResultSet is empty or not scrollable");
        }

        int rowNumb = rs.getRow();
        var rsmd = rs.getMetaData();
        int columnS = rsmd.getColumnCount();

        rs.beforeFirst();

        List<List<String>> outer = new ArrayList<>();


        int i = 0;
        while (rs.next() && i < rowNumb && rowNumb < 100) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < columnS; j++) {
                row.add(rs.getString(j + 1));
            }
            outer.add(row);
            i++;
        }
        rs.close();
        return outer;
    }
}
