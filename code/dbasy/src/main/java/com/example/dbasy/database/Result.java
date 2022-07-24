package com.example.dbasy.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Result is used for a Database query
 *
 * If you send a {@link com.example.dbasy.database.Database#request(String) request} to the Database, it will respond
 * with a Result.
 *
 * The data of the Result can <b>not</b> be changed
 */
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


    /**
     * Gets the Database which the result is from
     * @return source database
     */
    public Database getSource() {
        return source;
    }

    /**
     * Returns a list of {@link com.example.dbasy.database.Column columns} from the result
     * @return ArrayList of columns
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Returns a list of column names from the result
     * @return ArrayList of Names
     */
    public List<String> getColumnNames() {
        var list = new ArrayList<String>();
        this.columns.forEach((value) -> list.add(value.name));
        return list;
    }

    /**
     * Returns a 2D List of the content from the Result
     * @return 2D List of content
     */
    public List<List<String>> getContent() {
        return content;
    }

    /**
     * Gets the original SQL Statement used in the Result
     * @return String sql
     */
    public String getStatement() {
        return statement;
    }

    /**
     * See if the Result is Invalid
     * @return true if invalid
     */
    public boolean isInvalid() {
        return !invalid;
    }

    /**
     * See if the Result is loaded
     *
     * This is the case when you reload or change the result
     *
     * @return true if loaded
     */
    public boolean isLoaded() {
        return loaded;
    }


}
