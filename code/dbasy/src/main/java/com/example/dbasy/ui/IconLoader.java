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
}
