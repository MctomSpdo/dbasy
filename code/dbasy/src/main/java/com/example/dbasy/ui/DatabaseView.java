package com.example.dbasy.ui;

import com.example.dbasy.database.Database;

public class DatabaseView {
    private Database db;

    public DatabaseView(Database db) {
        this.db = db;
    }

    public Database getDb() {
        return db;
    }

    @Override
    public String toString() {
        return db.getDetails().database + "@" + db.getDetails().host;
    }
}
