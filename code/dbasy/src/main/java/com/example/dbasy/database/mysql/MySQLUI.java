package com.example.dbasy.database.mysql;

import com.example.dbasy.database.DBUI;
import javafx.scene.image.Image;

public class MySQLUI extends DBUI {
    public static Image icon = null;

    @Override
    public Image getIcon() {
        if(icon == null) {
            icon = new Image(String.valueOf(MySQLUI.class.getResource("icon-24.png")));
        }
        return icon;
    }
}
