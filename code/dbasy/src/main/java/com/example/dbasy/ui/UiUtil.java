package com.example.dbasy.ui;

import com.example.dbasy.Resources;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.invalid.InvalidDatabase;
import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.sql.SQLException;

public class UiUtil {
    /**
     * Shows the connection Dialog to the user and adds the database when successfull
     * @param resources resources to add the db to (and log)
     * @return Database added db
     */
    public static Database connectionDialog(Resources resources) {
        Database db;
        try {
            var connectController = new ConnectController();
            db = connectController.showDialog();
            if(!(db instanceof InvalidDatabase)) {
                resources.connections.add(db);
                try {
                    db.connect();
                } catch (SQLException e) {
                    resources.log.info("Default connection failed: ", e);
                }
            }
        } catch (IOException ignored) {
            db = new InvalidDatabase();
        }
        return db;
    }

    public static TreeItem getLoadingTreeItem() {
        return new TreeItem("Loading...");
    }

    public static TreeItem getErrorTreeItem() {
        return new TreeItem("Error");
    }
}
