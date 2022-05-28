package com.mctom.dbasy.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DBCollecion {

    private final List<Database> connections = new ArrayList<>();

    public int closeAll() {
        AtomicInteger failed = new AtomicInteger();
        connections.forEach(database -> {
            try {
                database.close();
            } catch (SQLException e) {
                failed.getAndIncrement();
            }
        });
        return failed.get();
    }

    public void add(Database db) {
        this.connections.add(db);
    }

    public void remove(Database db) {
        this.connections.remove(db);
    }

    public void removeAndClose(Database db) throws SQLException {
        this.connections.remove(db);
        db.close();
    }
}
