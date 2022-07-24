package com.example.dbasy.database;

import com.example.dbasy.Main;
import com.example.dbasy.Resources;
import com.example.dbasy.database.mysql.MySQLDatabase;
import com.example.dbasy.ui.ContextItem;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Database implements ContextItem {
    protected Connection conn;
    protected ConnectionDetails details;
    protected boolean active = false;

    //numbering of db for equals
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final int number;

    public Database() {
        this.number = counter.incrementAndGet();
    }

    public Database(ConnectionDetails details) {
        this();
        this.details = details;
    }

    //<editor-fold desc="Getter and Setter">
    public Connection getConn() {
        return conn;
    }

    public ConnectionDetails getDetails() {
        return details;
    }

    public void setDetails(ConnectionDetails details) {
        this.details = details;
    }

    /**
     * Set the name of the connection
     * @param name Name
     */
    public void setName(String name) {
        if(this.details == null) {
            this.details = new ConnectionDetails();
        }
        this.details.name = name;
    }
    //</editor-fold>

    //<editor-fold desc="Connect and close">
    public void connect() throws SQLException {
        Main.RESOURCES.log.info("Starting DB Connection: " + this.details.host + ":" + this.details.port);
        this.active = true;
        this.conn = DriverManager.getConnection(getUrl(this.details), this.details.username, this.details.password);
    }

    public void close() throws SQLException {
        Main.RESOURCES.log.info("Closing DB Connection: " + this.details.host + ":" + this.details.port);
        this.active = false;
        this.conn.close();
    }

    /**
     * completely close and removes the database
     *
     * when there is an error, it will still remove the database from resources
     * @throws SQLException on error
     */
    public void remove() throws SQLException {
        Main.RESOURCES.connections.remove(this);
        Platform.runLater(() -> Main.getController().UpdateDataBaseList());
        close();
    }
    //</editor-fold>

    /**
     * Gets the Tables that are loaded in the Database
     * @return List of Table
     * @throws SQLException on error
     */
    public List<Table> getTables() throws SQLException {
        var tables = this.conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
        var tableList = new ArrayList<Table>();
        while (tables.next()) {
            String catalog = tables.getString("TABLE_CAT");
            String schema = tables.getString("TABLE_SCHEM");
            String tableName = tables.getString("TABLE_NAME");
            var table = new Table(tableName, this);

            //primary key columns:
            try (ResultSet primaryKeys = this.conn.getMetaData().getPrimaryKeys(catalog, schema, tableName)) {
                while (primaryKeys.next()) {
                    var column = new Column(table, primaryKeys.getString("COLUMN_NAME"));
                    column.keys.add(new Key(column, "", Key.Type.PRIMARY));
                    table.addColumnIfNotExists(column);
                }
            }

            try (ResultSet exportedKeys = this.conn.getMetaData().getExportedKeys(catalog, schema, tableName)){
                while(exportedKeys.next()) {
                    var column = new Column(table, exportedKeys.getString("PKCOLUMN_NAME"));
                    String referenceName = exportedKeys.getString("FKTABLE_NAME") + "."  + exportedKeys.getString("FKCOLUMN_NAME");
                    column.keys.add(new Key(column,  referenceName, Key.Type.FOREIGN));

                    if(!table.addColumnIfNotExists(column)) {
                        var index = table.getColumns().indexOf(table);
                        if(index != -1) {
                            var existingColumn = table.getColumns().get(index);
                            existingColumn.keys.add(new Key(column, referenceName, Key.Type.FOREIGN));
                        }
                    }

                    /*var metadata = exportedKeys.getMetaData();
                    var amount = metadata.getColumnCount();
                    for(int i = 1; i <= amount; i++) {
                        System.out.println(metadata.getColumnName(i));
                    }
                    System.out.println(); */
                }
            }
            // similar for exportedKeys
            tableList.add(table);
        }
        return tableList;
    }

    //<editor-fold desc="Abstract Methods">
    /**
     * Returns the name of the Database
     * @return DatabaseName
     */
    @Override
    public abstract String toString();

    /**
     * Returns the default Port used by the Database application
     * @return port for connection
     */
    public abstract int getPort();

    /**
     * Gets the Database String (URL) for a given Database
     * @param details ConnectionDetails to base the url of
     * @return URL
     */
    public abstract String getUrl(ConnectionDetails details);

    /**
     * Gets a new instance of the current Database
     * @return new Database instance
     */
    public abstract Database getNewInstance();

    /**
     * Returns the UI Components for the Database
     * @return UI components
     */
    public abstract DBUI getUI();

    /**
     * Loads only the headers for a given Table
     * @param table table
     * @return same table with headers
     * @throws SQLException on error
     */
    public abstract Table loadColumns(Table table) throws SQLException;

    /**
     * Loads the Table, with the given limits and offsets
     * @param table table to load
     * @param limit limit
     * @param offset offset
     * @return loaded Table
     * @throws SQLException on error
     */
    public abstract Table loadTable(Table table, int limit, int offset) throws SQLException;

    public abstract Result request(String sql) throws SQLException;
    //</editor-fold>

    /**
     * Ads the Databases to a static list of Databases, for the user to pick one
     */
    public static void addDatabases() {
        Main.RESOURCES.log.debug("Adding Database select options");
        var mySQL = new MySQLDatabase();
        mySQL.setName("MySQL");
        Resources.repoList.add(mySQL);
    }

    /**
     * Gets the Columns from a given ResultSet
     * @param rs ResultSet
     * @return List of Columns
     * @throws SQLException on error
     */
    public List<Column> columnsFromResult(ResultSet rs) throws SQLException {
        var columns = new ArrayList<Column>();
        var metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for(int i = 1; i < columnCount + 1; i++) {
            columns.add(new Column(new Table(metaData.getTableName(i), true), metaData.getColumnName(i)));
        }
        return columns;
    }

    //<editor-fold desc="Result Processing">
    /**
     * Gets the headers from a given ResultSet (column names)
     * @param rs ResultSet
     * @return headers
     * @throws SQLException on error
     */
    public static List<String> headersFromResult(ResultSet rs) throws SQLException {
        var headers = new ArrayList<String>();
        var metaData = rs.getMetaData();
        int columns = metaData.getColumnCount();

        for(int i = 1; i < columns + 1; i++) {
            headers.add(metaData.getColumnName(i));
        }
        return headers;
    }

    /**
     * Moves the content from a ResultSet into a 2D ArrayList
     *
     * The <b>first dimension</b> are the rows,
     * The <b>second dimension</b> are the columns
     *
     * Be Careful, for long ResultSets this might take a while, so performing this async is best
     * @param rs ResultSet from the Request
     * @return 2D ArrayList of the set
     * @throws SQLException on error
     */
    public static List<List<String>> contentFromResult(ResultSet rs) throws SQLException {
        try {
            rs.last();
        } catch (SQLException noResult) {
            throw new IllegalArgumentException("ResultSet is empty or not scrollable");
        }

        int rowNumb = rs.getRow();
        var rsmd = rs.getMetaData();
        int columnS = rsmd.getColumnCount();

        rs.beforeFirst();

        List<List<String>> outer = new ArrayList<>();

        int i = 0;
        while (rs.next() && i < rowNumb) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < columnS; j++) {
                row.add(rs.getString(j + 1));
            }
            outer.add(row);
            i++;
        }
        return outer;
    }
    //</editor-fold>

    @Override
    public ContextMenu getContextMenu() {
        return getUI().getContextMenu(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Database database = (Database) o;
        return number == database.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
