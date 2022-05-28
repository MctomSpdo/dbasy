package com.mctom.dbasy.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Database {

    protected Connection conn;
    protected ConnectionDetails connectionDetails;

    //<editor-fold desc="Constructor">
    public Database(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    public Database() {
    }
    //</editor-fold>

    /**
     * sets the connectionDetails. If the connection is currently active, it will be closed
     *
     * If the closing fails, the connectionDetails won't be set
     * @param connectionDetails connectionDetails for the database
     */
    public void setDetails(ConnectionDetails connectionDetails) {
        try {
            if(this.conn != null && this.conn.isClosed()) {
                this.conn.close();
            }
        } catch (SQLException ignored) {

        }
        this.connectionDetails = connectionDetails;
    }

    /**
     * Connects to the database with the given connectionDetails
     * @throws SQLException if connections fails
     */
    public void connect() throws SQLException {
        this.conn = DriverManager.getConnection(getDBString(), this.connectionDetails.getUser(), this.connectionDetails.getPassword());
    }

    /**
     * Closes the database connections
     * @throws SQLException if closing failed
     */
    public void close() throws SQLException {
        conn.close();
    }

    /**
     * Gets a new instance of the database object
     * @return new instance
     */
    public abstract Database getNewInstance();

    /**
     * Gets the JDBC database string for a given Database
     * @return database string
     */
    public String getDBString() {
        if(this.connectionDetails == null) {
            return null;
        }
        return getDBString(this.connectionDetails);
    }
    public abstract String getDBString(ConnectionDetails details);

    /**
     * Gets the default port used for the given database
     * @return int default port
     */
    public abstract int getDefaultPort();

    public abstract ConnectionDetails getDataFromConnectString(String connectString);

    @Override
    public abstract String toString();
}
