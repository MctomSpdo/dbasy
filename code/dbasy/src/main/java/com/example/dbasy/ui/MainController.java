package com.example.dbasy.ui;

import com.example.dbasy.Resources;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainController {
    Resources resources;

    @FXML
    private MenuBar mbMain;

    @FXML
    private VBox root;

    @FXML
    private Tab tabConsole;

    @FXML
    private TabPane tbResults;

    @FXML
    private TreeView trDatabases;

    @FXML
    public void initialize() {
        resources.log.debug("Loading Main Display components");

        trDatabases.setShowRoot(false);
        trDatabases.setRoot(new TreeItem());
        refreshDBTree(true);
    }

    public MainController(Resources resources) {
        resources.controller = this;
        this.resources = resources;
    }

    public void show(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader(MainController.class.getResource("main-view.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("DBasy");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads the TreeView
     * @param doRefresh if Tables and contents should be refreshed
     */
    public void refreshDBTree(boolean doRefresh) {
        var databases = resources.connections;
        databases.forEach((database -> {
            var view = new DatabaseView(database);
            var treeItem = new TreeItem(view);
            //set values for Database icon:
            treeItem.setGraphic(new ImageView(database.getUI().getIcon()));
            treeItem.setExpanded(true);
            //set child elements:
            var dbItem = new TreeItem<>("db");
            dbItem.setExpanded(true);
            var tablesItem = new TreeItem<>("tables");
            tablesItem.getChildren().add(UiUtil.getLoadingTreeItem());
            tablesItem.expandedProperty().addListener((observableValue, aBoolean, t1) -> {
                //if treeItem was opened and first element is Loading...
                if(t1 || doRefresh) {
                    var firstItem = tablesItem.getChildren().get(0);
                    if(doRefresh || firstItem == null || firstItem.getValue().equals(UiUtil.getLoadingTreeItem().getValue())) {
                        //load Tables from Database:
                        try {
                            tablesItem.getChildren().clear();
                            var tableList = database.getTables();
                            tableList.forEach((table -> {
                                var tableItem = new TreeItem(table);
                                tablesItem.getChildren().add(tableItem);
                                //children of database table:
                                var columnsItem = new TreeItem("columns");
                                columnsItem.getChildren().add(UiUtil.getLoadingTreeItem());
                                tableItem.getChildren().add(columnsItem);
                                //TODO: load columns
                            }));
                        } catch (SQLException e) {
                            resources.log.error("Could not load Tables from" + database, e);
                        }
                    }
                }
            });

            dbItem.getChildren().add(tablesItem);

            treeItem.getChildren().add(dbItem);
            trDatabases.getRoot().getChildren().add(treeItem);
        }));
    }
}