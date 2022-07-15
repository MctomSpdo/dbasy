package com.example.dbasy.database;

import com.example.dbasy.database.invalid.InvalidDatabase;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Table {
    String name;
    private Database source;
    private List<String> headers = new ArrayList<>();
    private List<List<String>> content = new ArrayList<>();

    private boolean invalid = false;
    private boolean loaded = false;

    //<editor-fold desc="Constructor">
    public Table(String name, Database source) {
        this.name = name;
        this.source = source;
    }

    public Table(String name, boolean invalid) {
        this.name = name;
        if(!invalid) {
            throw new IllegalArgumentException("Table has to be Invalid");
        }
        this.invalid = invalid;
    }
    //</editor-fold>

    //<editor-fold desc="Getter">
    public String getName() {
        return name;
    }

    public Database getSource() {
        return source;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<List<String>> getContent() {
        return content;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public boolean isLoaded() {
        return loaded;
    }
    //</editor-fold>

    /**
     * Loads the Tables from the source. This does require an active Database connection
     */
    public void load() {
        //TODO: implement later
    }

    /**
     * Loads the content and headers from a given ResultSet
     * @param rs ResultSet rs
     */
    public void load(ResultSet rs) {
        //TODO: implement later
    }

    /**
     * Loads the table from given headers and contents
     * @param headers headers to load Table from
     * @param content Content to load Tables from
     */
    public void load(List<String> headers, List<List<String>> content) {
        this.loaded = true;
        this.headers = headers;
        this.content = content;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Transforms a List of Strings to a List of Tables, when also given a Database as source
     * @param tableNames List of Tablenames
     * @param source source of the List
     * @return List of Tables
     */
    public static List<Table> getTables(List<String> tableNames, Database source) {
        if(source instanceof InvalidDatabase) throw new IllegalArgumentException("Source has to be a valid Database");

        return tableNames
                .stream()
                .map((tableName) -> new Table(tableName, source))
                .toList();
    }
}
