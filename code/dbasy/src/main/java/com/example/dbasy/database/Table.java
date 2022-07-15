package com.example.dbasy.database;

import com.example.dbasy.database.invalid.InvalidDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Table {
    String name;
    private Database source;
    private List<String> headers = null;
    private List<List<String>> content = null;

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

    //<editor-fold desc="Getter and Setter">
    public String getName() {
        return name;
    }

    public Database getSource() {
        return source;
    }

    public List<String> getHeaders() throws SQLException {
        if(headers == null) {
            this.source.loadHeaders(this);
        }
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

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void setContent(List<List<String>> content) {
        this.content = content;
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
    public void load(ResultSet rs) throws SQLException {
        this.loaded = true;
        this.headers = Database.headersFromResult(rs);
        this.content = Database.contentFromResult(rs);
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
