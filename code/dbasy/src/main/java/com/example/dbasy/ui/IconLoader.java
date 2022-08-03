package com.example.dbasy.ui;

import javafx.scene.image.Image;

public class IconLoader {

    private static Image table = null;
    public static Image getTable() {
        if(table == null) {
            table = new Image(String.valueOf(IconLoader.class.getResource("icons/table.png")));
        }
        return table;
    }

    private static Image folder = null;
    public static Image getFolder() {
        if(folder == null) {
            folder = new Image(String.valueOf(IconLoader.class.getResource("icons/folder.png")));
        }
        return folder;
    }

    private static Image column = null;

    public static Image getColumn() {
        if(column == null) {
            column = new Image(String.valueOf(IconLoader.class.getResource("icons/column.png")));
        }
        return column;
    }

    private static Image database = null;

    public static Image getDatabase() {
        if(database == null) {
            database = new Image(String.valueOf(IconLoader.class.getResource("icons/database.png")));
        }
        return database;
    }

    private static Image warning = null;

    public static Image getWarning() {
        if(warning == null) {
            warning = new Image(String.valueOf(IconLoader.class.getResource("icons/warning.png")));
        }
        return warning;
    }

    private static Image warningFull = null;

    public static Image getWarningFull() {
        if(warningFull == null) {
            warningFull = new Image(String.valueOf(IconLoader.class.getResource("icons/warning_full.png")));
        }
        return warningFull;
    }

    private static Image console = null;

    public static Image getConsoleItem() {
        if(console == null) {
            console = new Image(String.valueOf(IconLoader.class.getResource("icons/console.png")));
        }
        return console;
    }

    private static Image key = null;

    public static Image getKey() {
        if(key == null) {
            key = new Image(String.valueOf(IconLoader.class.getResource("icons/key.png")));
        }
        return key;
    }

    private static Image reload = null;

    public static Image getReload() {
        if(reload == null) {
            reload = new Image(String.valueOf(IconLoader.class.getResource("icons/reload.png")));
        }
        return reload;
    }
}
