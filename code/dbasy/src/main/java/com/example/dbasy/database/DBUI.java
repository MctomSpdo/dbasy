package com.example.dbasy.database;

import com.example.dbasy.Main;
import com.example.dbasy.ui.UiUtil;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;

import java.sql.SQLException;
import java.util.regex.Pattern;

public abstract class DBUI {
    /**
     * Gets the Icon of the Database that is in use.
     * @return Image DatabaseIcon
     */
    public abstract Image getIcon();

    /**
     * Get Pattern for highlighting sql code
     *
     * There are different sections in the pattern:
     *
     * <b>KEYWORD:</b> These are all the SQL Keywords used for the database
     * <b>FUNCTION:</b> All the functions used in this database
     * <b>BRACE: </b> Typical { and }
     * <b>BRACKET: </b> The Brackets used in the Language (Typical ( and ))
     * <b>SEMICOLON: </b> Marks the Semicolon
     * <b>STRING: </b> Marks a String in the SQL Syntax
     * <b>COMMENT: </b> Marks a Comment in the SQL Syntax
     * @return Pattern
     */
    public abstract Pattern getPattern();

    /**
     * Returns an array of keywords used in the database
     *
     * This could be for example: {"SELECT", "DROP", "ALTER", "INTO", ...}
     *
     * following rules apply to keywords
     * - single words (no spaces, so "insert into" would be {"INSERT", "INTO"}
     * - all uppercase
     * - plain sql keywords, no functions
     * - sorted alphabetically
     *
     * @return StringArray with keywords
     */
    public abstract String[] getKeywords();

    /**
     * Returns an array of function names used in the database
     *
     * This could be for example: {"MIN", "MAX", "CONCAT", ...}
     *
     * following rules apply to function names:
     * - single words (since sql does require that too)
     * - all uppercase
     * - sorted alphabetically
     * @return StringArray with function names
     */
    public abstract String[] getFunctions();

    //<editor-fold desc="Context Menu">

    /**
     * Gives the Context Menu for right-clicking on a database
     * @param caller Database which was clicked
     * @return ContextMenu to display
     */
    public ContextMenu getContextMenu(Database caller) {
        var menu = new ContextMenu();

        //new Menu:
        menu.getItems().add(getNewMenu(caller));

        //rename Menu:
        var renameDbItem = new MenuItem("Rename");
        renameDbItem.setOnAction(event -> UiUtil.renameDatabaseDialog(caller));
        menu.getItems().add(renameDbItem);

        //refresh db:
        var refreshDBItem = new MenuItem("Refresh");
        refreshDBItem.setOnAction(actionEvent -> Main.RESOURCES.controller.refreshDBTree(true));
        menu.getItems().add(refreshDBItem);

        //duplicate db:
        var duplicateDBItem = new MenuItem("Duplicate");
        //TODO: add logic
        menu.getItems().add(duplicateDBItem);

        //separator
        menu.getItems().add(new SeparatorMenuItem());

        //close connection:
        var closeConnectionDBItem = new MenuItem("Disconnect");
        menu.getItems().add(closeConnectionDBItem);

        //remove db
        var removeDbItem = new MenuItem("Remove");
        removeDbItem.setOnAction((actionEvent) -> {
            if(UiUtil.conformationDialog("Database '" + caller.getDetails().name + "' will be removed")) {
                (new Thread(() -> {
                    try {
                        caller.remove();
                    } catch (SQLException e) {
                        Main.RESOURCES.log.error("Could not remove connection: ", e);
                    }
                })).start();
            }
        });
        menu.getItems().add(removeDbItem);

        return menu;
    }

    /**
     * Gets the new menu when clicking on a database.
     *
     * This is a submenu of the Menu returned by {@link #getContextMenu(Database)}
     * @param caller Database that was clicked
     * @return Submenu "New"
     */
    protected Menu getNewMenu(Database caller) {
        var menu = new Menu("New");

        //new query console and separator:
        var consoleItem = new MenuItem("query console");
        consoleItem.setOnAction((actionEvent) -> Main.getController().newCodeTab(caller));
        menu.getItems().addAll(consoleItem, new SeparatorMenuItem());

        //<editor-fold desc="inside DB">
        //new table:
        var newTableItem = new MenuItem("Table");
        //TODO: New Table UI
        menu.getItems().add(newTableItem);

        //new view:
        var newViewItem = new MenuItem("View");
        //TODO: New View UI
        menu.getItems().add(newViewItem);
        //</editor-fold>

        //separator:
        menu.getItems().add(new SeparatorMenuItem());

        //newDB
        var newDatabaseItem = new MenuItem("Database");
        newDatabaseItem.setOnAction((actionEvent) -> (new Thread(() -> Platform.runLater(() -> {
            UiUtil.connectionDialog(Main.RESOURCES);
            Main.getController().refreshDBTree(false);
        }))).start());
        menu.getItems().add(newDatabaseItem);

        return menu;
    }
    //</editor-fold>
}
