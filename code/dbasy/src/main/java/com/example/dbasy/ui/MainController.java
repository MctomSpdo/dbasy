package com.example.dbasy.ui;

import com.example.dbasy.Main;
import com.example.dbasy.Resources;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    public MainController(Resources resources) {
        resources.controller = this;
        this.resources = resources;
    }

    public void show(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("main-view.fxml"));
        fxmlLoader.setController(this);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("DBasy");
        stage.setScene(scene);
        stage.show();
    }
}