package com.mctom.dbasy.db;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private Connection conn;
    ConnectionDetails connectionDetails;

    public Database(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    /**
     * sets the connectionDetails. If the connection is currently active, it will be closed
     *
     * If the closing fails, the connectionDetails won't be set
     * @param connectionDetails connectionDetails for the database
     * @throws SQLException if connection can't be closed
     */
    public void setConnectionDetails(ConnectionDetails connectionDetails) throws SQLException {
        if(!this.conn.isClosed()) {
            this.conn.close();
        }
        this.connectionDetails = connectionDetails;
    }

    public void connect() throws SQLException {
        throw new SQLException();
    }

    public void close() throws SQLException {
        conn.close();
    }
}
