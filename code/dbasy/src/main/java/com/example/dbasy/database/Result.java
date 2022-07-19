package com.example.dbasy.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Result {
    protected Database source;
    protected List<String> headers = null;
    protected List<List<String>> content = null;
    protected boolean invalid = false;
    protected boolean loaded = false;

    protected Result() {

    }

    public Result (boolean invavlid) {
        if(!invalid) {
            throw new IllegalArgumentException("Table has to be Invalid");
        }
        this.invalid = true;
    }

    public Result(Database source, List<String> headers, List<List<String>> content) {
        this.source = source;
        this.headers = headers;
        this.content = content;
        this.loaded = true;
    }

    public Result(Database source, ResultSet rs) throws SQLException {
        this(source, Database.headersFromResult(rs), Database.contentFromResult(rs));
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


}
