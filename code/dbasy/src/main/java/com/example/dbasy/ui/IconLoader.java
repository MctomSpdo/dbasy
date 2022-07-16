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
}
