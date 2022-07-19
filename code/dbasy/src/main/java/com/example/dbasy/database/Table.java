package com.example.dbasy.database;

import com.example.dbasy.database.invalid.InvalidDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Table extends Result {
    String name;

    //<editor-fold desc="Constructor">
    public Table(String name, Database source) {
        super();
        this.name = name;
        this.source = source;
    }

    public Table(String name, boolean invalid) {
        super(invalid);
        this.name = name;
    }
    //</editor-fold>

    //<editor-fold desc="Getter and Setter">
    public String getName() {
        return name;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getAndLoadHeaders() throws SQLException {
        if(headers == null) {
            this.source.loadHeaders(this);
        }
        return headers;
    }

    public void setContent(List<List<String>> content) {
        this.content = content;
    }
    //</editor-fold>

    //<editor-fold desc="loaders">
    /**
     * Loads the Tables from the source. This does require an active Database connection
     */
    public void load(int limit, int offset) throws SQLException {
        this.source.loadTable(this, limit, offset);
    }

    /**
     * Loads the content and headers from a given ResultSet
     *
     * @param rs ResultSet rs
     */
    public void load(ResultSet rs) throws SQLException {
        this.loaded = true;
        this.headers = Database.headersFromResult(rs);
        this.content = Database.contentFromResult(rs);
    }

    /**
     * Loads the table from given headers and contents
     *
     * @param headers headers to load Table from
     * @param content Content to load Tables from
     */
    public void load(List<String> headers, List<List<String>> content) {
        this.loaded = true;
        this.headers = headers;
        this.content = content;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return name;
    }

    /**
     * Transforms a List of Strings to a List of Tables, when also given a Database as source
     *
     * @param tableNames List of TableNames
     * @param source     source of the List
     * @return List of Tables
     */
    public static List<Table> getTables(List<String> tableNames, Database source) {
        if (source instanceof InvalidDatabase) throw new IllegalArgumentException("Source has to be a valid Database");

        return tableNames
                .stream()
                .map((tableName) -> new Table(tableName, source))
                .toList();
    }
}
