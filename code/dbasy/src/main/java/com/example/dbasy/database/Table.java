package com.example.dbasy.database;

import com.example.dbasy.database.invalid.InvalidDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Table extends Result {
    String name;
    public boolean columnsLoaded = false;

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

    public void setColumns(List<Column> headers) {
        this.columns = headers;
    }

    public List<Column> getAndLoadColumns() throws SQLException {
        if(this.columns == null) {
            this.source.loadColumns(this);
            this.columnsLoaded = true;
        }
        if(!this.columnsLoaded) {
            this.source.loadColumns(this);
            this.columnsLoaded = true;
        }
        return this.columns;
    }

    /**
     * Adds a Columns to the List, when it does not exist
     * @param column Column to add
     * @return true if it got added, false otherwise
     */
    public boolean addColumnIfNotExists(Column column) {
        if(this.columns == null) {
            this.columns = new ArrayList<>();
            this.columns.add(column);
            return true;
        }
        if(!this.columns.contains(column)) {
            this.columns.add(column);
            return true;
        }
        return false;
    }

    public List<String> getAndLoadColumnNames() throws SQLException {
        return getAndLoadColumns()
                .stream()
                .map((value) -> value.name)
                .toList();
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
     * Loads the table from given headers and contents
     *
     * @param columns headers to load Table from
     * @param content Content to load Tables from
     */
    public void load(List<Column> columns, List<List<String>> content) {
        this.loaded = true;
        this.columns = columns;
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
