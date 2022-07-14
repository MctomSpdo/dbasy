package com.example.dbasy;

import com.example.dbasy.ui.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainController c = new MainController();
        c.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}