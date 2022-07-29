package com.example.dbasy.database;

import com.example.dbasy.Main;
import com.example.dbasy.database.invalid.InvalidDatabase;
import com.example.dbasy.ui.ContextItem;
import com.example.dbasy.ui.UiUtil;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tables of a {@link com.example.dbasy.database.Database database}
 */
public class Table extends Result implements ContextItem {
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

    /**
     * Returns the Columns in the Table.
     * If the Columns are currently not loaded, it will load them.
     * @return List of columns
     * @throws SQLException on error (only when loaded and not cached)
     */
    public List<Column> getAndLoadColumns() throws SQLException {
        if(this.columns == null || !this.columnsLoaded) {
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

    /**
     * Gets the columns names from the Table
     * If the columns are currently not loaded, it will load the columns from the database
     * @return List of ColumnNames
     * @throws SQLException on error (only occurs when loading columns)
     */
    public List<String> getAndLoadColumnNames() throws SQLException {
        return getAndLoadColumns()
                .stream()
                .map((value) -> value.name)
                .toList();
    }

    /**
     * Sets the content of the Table
     * @param content contents of the Table
     */
    public void setContent(List<List<String>> content) {
        this.content = content;
    }

    public List<Key> getKeys () {
        var list = new ArrayList<Key>();
        var columns = getColumns();
        if(columns != null) {
            columns.forEach(value -> {
                list.addAll(value.keys);
            });
        }
        return list;
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

    public void drop() throws SQLException {
        this.source.dropTable(this);
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

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public ContextMenu getContextMenu() {
        var menu = new ContextMenu();

        //drop table
        var dropItem = new MenuItem("Drop");
        dropItem.setOnAction(actionEvent -> {
            if(UiUtil.conformationDialog("Table '" + getName() + "' will be dropped")) {
                (new Thread(() -> {
                    try {
                        drop();
                        Platform.runLater(() -> {
                            Main.getController().refreshDBTree(true);
                        });
                    } catch (SQLException e) {
                        Main.RESOURCES.log.error("Could not Drop table: " + getName() + ": ", e);
                    }
                })).start();
            }
        });
        menu.getItems().add(dropItem);

        //export table
        var exportItem = new MenuItem("Export");
        exportItem.setOnAction(actionEvent -> {
            (new Thread(() -> {
                Platform.runLater(() -> {
                    UiUtil.exportTableDialog(this);
                });
            })).start();
        });
        menu.getItems().add(exportItem);

        return menu;
    }
}
