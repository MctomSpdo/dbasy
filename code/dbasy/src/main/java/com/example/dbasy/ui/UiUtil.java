package com.example.dbasy.ui;

import com.example.dbasy.Main;
import com.example.dbasy.Resources;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.Table;
import com.example.dbasy.database.invalid.InvalidDatabase;
import com.example.dbasy.file.FileUtil;
import com.example.dbasy.ui.dialogs.ConformationDialog;
import com.example.dbasy.ui.dialogs.ConnectDialog;
import com.example.dbasy.ui.dialogs.RenameDatabaseDialog;
import com.example.dbasy.ui.dialogs.TableExportDialog;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.sql.SQLException;

public class UiUtil {
    /**
     * Shows the connection Dialog to the user and adds the database when successful
     * @param resources resources to add the db to (and log)
     * @return Database added db
     */
    public static Database connectionDialog(Resources resources) {
        Database db;
        try {
            var connectController = new ConnectDialog();
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

    public static TreeItem getColumnTreeItem(Table table) {
        var columnsItem = new TreeItem<>("columns");
        columnsItem.getChildren().add(UiUtil.getLoadingTreeItem());
        columnsItem.expandedProperty().addListener((observableValue1, aBoolean1, t11) -> {
            if(t11) {
                var firstColumnItem = columnsItem.getChildren().get(0);
                if(firstColumnItem == null || firstColumnItem.getValue() instanceof String) {
                    (new Thread(() -> {
                        try {
                            var headers = table.getAndLoadColumns();
                            Platform.runLater(() -> {
                                columnsItem.getChildren().clear();
                                headers.forEach((header) -> {
                                    var columnItem = new TreeItem(header);
                                    columnItem.setGraphic(UiUtil.getSizedImage(IconLoader.getColumn()));
                                    columnsItem.getChildren().add(columnItem);
                                });
                            });
                        } catch (SQLException e) {
                            Main.RESOURCES.log.error("Could not load Columns from Database: " + table.getSource(), e);
                        }
                    })).start();
                }
            }
        });
        //set icon:
        columnsItem.setGraphic(getSizedImage(IconLoader.getFolder()));
        return columnsItem;
    }

    public static ImageView getSizedImage(Image image) {
        var imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(17);
        return imageView;
    }

    /**
     * gets an entire SQL statement from the text from the given caret position
     *
     * @param text  text
     * @param caret index
     * @return SQL statement
     * @throws IllegalArgumentException if index < 0 or index is longer then word
     */
    public static String getSqlStatementFromPosition(String text, int caret) throws IllegalArgumentException {
        if (caret > text.length() || caret < 0) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder();
        char[] arr = text.toCharArray();

        //add characters before the index:
        for (int i = caret - 1; i >= 0; i--) {
            if (arr[i] == '\n') break;
            sb.append(arr[i]);
        }
        sb.reverse(); //reverse since the for loop is going in reverse direction

        //add characters after index and index itself:
        for (int i = caret; i < text.length(); i++) {
            if (arr[i] == ';' || arr[i] == '\n') break;
            sb.append(arr[i]);
        }

        return sb.toString();
    }

    public static void renameDatabaseDialog(Database db) {
        try {
            var dialog = new RenameDatabaseDialog(db);
            if(dialog.showDialog()) {
                Main.getController().refreshDBTree(false);
            }
        } catch (IOException e) {
            Main.RESOURCES.log.fatal("Could not load internal Files ", e);
        }
    }

    public static boolean conformationDialog(String text) {
        try {
            var dialog = new ConformationDialog(text);
            return dialog.showDialog();
        } catch (IOException e) {
            Main.RESOURCES.log.fatal("Could not load internal Files", e);
        }
        return false;
    }

    public static boolean exportTableDialog(Table table) {
        try {
            //show export dialog:
            var dialog = new TableExportDialog(table);
            var result = dialog.showDialog();

            //on cancel
            if(result == null) return false;

            //show FileChooser
            var type = result.usedExporter().getFileType();
            FileChooser fc = new FileChooser();
            fc.setInitialFileName("data." + type);
            var filter = new FileChooser.ExtensionFilter(type + " Files", "*." + type);
            fc.getExtensionFilters().add(filter);

            var file = fc.showSaveDialog(Main.getMainStage());

            FileUtil.saveString(file, result.result());
        } catch (IOException e) {
            Main.RESOURCES.log.fatal("Could not load file: ", e);
        }
        return false;
    }
}
