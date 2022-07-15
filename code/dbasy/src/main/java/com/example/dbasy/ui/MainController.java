package com.example.dbasy.ui;

import com.example.dbasy.Resources;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

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
        refreshDBTree();
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
     * Refreshes the list of databases that are currently connected
     */
    public void refreshDBTree() {
        var databases = resources.connections;
        databases.forEach((database -> {
            var view = new DatabaseView(database);
            var treeItem = new TreeItem<>(view);
            treeItem.setGraphic(new ImageView(database.getUI().getIcon()));
            trDatabases.getRoot().getChildren().add(treeItem);
        }));
    }
}