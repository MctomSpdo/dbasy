package com.example.dbasy;

import com.example.dbasy.ui.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        var resources = new Resources();

        Logger logger = LogManager.getLogger(Resources.class);
        System.out.println(Resources.class.getResource("log4j2.xml"));
        logger.error("Logger started");

        //show new controller:
        var controller = new MainController(resources);
        controller.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}