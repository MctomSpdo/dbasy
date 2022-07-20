package com.example.dbasy.database;

public class Key {
    public enum Type {
        PRIMARY,
        FOREIGN
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