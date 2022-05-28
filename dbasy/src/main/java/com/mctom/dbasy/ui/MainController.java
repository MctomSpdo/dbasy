package com.mctom.dbasy.ui;

import com.mctom.dbasy.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private MenuBar mbMain;

    @FXML
    private VBox root;

    @FXML
    private Tab tabConsole;

    @FXML
    private TabPane tbResults;

    @FXML
    private TreeView<?> trDatabases;

    public void show(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("main-view.fxml"));
        fxmlLoader.setController(this);
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle(Application.PORGRAMMNAME);
        stage.setScene(scene);
        stage.show();
    }
}