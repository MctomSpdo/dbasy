package com.example.dbasy.database.exporter;

public enum ExporterRowSplitter {
    ENTER("enter", "\n"),
    TAB("tab", "\t");

    public final String name;
    public final String value;

    ExporterRowSplitter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
}
