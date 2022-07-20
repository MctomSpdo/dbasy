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

    /**
     * Set the name of the connection
     * @param name Name
     */
    public void setName(String name) {
        if(this.details == null) {
            this.details = new ConnectionDetails();
        }
        this.details.name = name;
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

    //<editor-fold desc="Abstract Methods">
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

    /**
     * Loads only the headers for a given Table
     * @param table table
     * @return same table with headers
     * @throws SQLException on error
     */
    public abstract Table loadHeaders(Table table) throws SQLException;

    /**
     * Loads the Table, with the given limits and offsets
     * @param table table to load
     * @param limit limit
     * @param offset offset
     * @return loaded Table
     * @throws SQLException on error
     */
    public abstract Table loadTable(Table table, int limit, int offset) throws SQLException;

    public abstract Result request(String sql) throws SQLException;
    //</editor-fold>

    /**
     * Ads the Databases to a static list of Databases, for the user to pick one
     */
    public static void addDatabases() {
        Main.RESOURCES.log.debug("Adding Database select options");
        var mySQL = new MySQLDatabase();
        mySQL.setName("MySQL");
        Resources.repoList.add(mySQL);
    }

    //<editor-fold desc="Result Processing">
    /**
     * Gets the headers from a given ResultSet (column names)
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

    /**
     * Moves the content from a ResultSet into a 2D ArrayList
     *
     * The <b>first dimension</b> are the rows,
     * The <b>second dimension</b> are the columns
     *
     * Be Careful, for long ResultSets this might take a while, so performing this async is best
     * @param rs ResultSet from the Request
     * @return 2D ArrayList of the set
     * @throws SQLException on error
     */
    public static List<List<String>> contentFromResult(ResultSet rs) throws SQLException {
        try {
            rs.last();
        } catch (SQLException noResult) {
            throw new IllegalArgumentException("ResultSet is empty or not scrollable");
        }

        int rowNumb = rs.getRow();
        var rsmd = rs.getMetaData();
        int columnS = rsmd.getColumnCount();

        rs.beforeFirst();

        List<List<String>> outer = new ArrayList<>();

        int i = 0;
        while (rs.next() && i < rowNumb) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < columnS; j++) {
                row.add(rs.getString(j + 1));
            }
            outer.add(row);
            i++;
        }
        return outer;
    }
    //</editor-fold>
}
