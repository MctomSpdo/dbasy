package com.example.dbasy.database;

/**
 * A key used in {@link com.example.dbasy.database.Column columns}.
 * There are 3 different Key types:
 *
 * 1. Primary key
 * 2. Foreign key
 * 3. unique index
 */
public class Key {
    public enum Type {
        PRIMARY,
        FOREIGN,
        UNIQUE
    }
    Column parent;
    String references;
    Type type;

    public Key(Column parent, String references, Type type) {
        this.parent = parent;
        this.references = references;
        this.type = type;
    }
}