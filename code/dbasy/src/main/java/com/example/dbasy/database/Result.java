package com.example.dbasy.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Result {
    protected Database source;
    protected List<Column> columns;
    protected List<List<String>> content = null;
    protected boolean invalid = false;
    protected boolean loaded = false;

    protected String statement;

    protected Result() {

    }

    public Result (boolean invalid) {
        if(!invalid) {
            throw new IllegalArgumentException("Table has to be Invalid");
        }
        this.invalid = true;
    }

    public Result(Database source, List<Column> columns, List<List<String>> content) {
        this.source = source;
        this.columns = columns;
        this.content = content;
        this.loaded = true;
    }

    public Result(Database source, ResultSet rs) throws SQLException {
        this(source, source.columnsFromResult(rs), Database.contentFromResult(rs));
    }

    public Result(Database source, ResultSet rs, String statement) throws SQLException {
        this(source, rs);
        this.statement = statement;
    }


    public Database getSource() {
        return source;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<String> getColumnNames() {
        var list = new ArrayList<String>();
        this.columns.forEach((value) -> list.add(value.name));
        return list;
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


}
