package com.example.dbasy.database;

import com.example.dbasy.Main;
import com.example.dbasy.ui.UiUtil;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;

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

    //<editor-fold desc="Context Menu">
    public ContextMenu getContextMenu(Database caller) {
        var menu = new ContextMenu();

        //new Menu:
        menu.getItems().add(getNewMenu(caller));

        //rename Menu:
        var renameDbItem = new MenuItem("Rename");
        renameDbItem.setOnAction((event -> UiUtil.renameDatabaseDialog(caller)));
        menu.getItems().add(renameDbItem);

        //refresh db:
        var refreshDBItem = new MenuItem("Refresh");
        //TODO: add logic
        menu.getItems().add(refreshDBItem);

        //duplicate db:
        var duplicateDBItem = new MenuItem("Duplicate");
        //TODO: add logic
        menu.getItems().add(duplicateDBItem);

        //separator
        menu.getItems().add(new SeparatorMenuItem());

        //close connection:
        var closeConnectionDBItem = new MenuItem("Disconnect");
        //TODO: add logic
        menu.getItems().add(closeConnectionDBItem);

        //remove db
        var removeDbItem = new MenuItem("Remove");
        menu.getItems().add(removeDbItem);

        return menu;
    }

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
