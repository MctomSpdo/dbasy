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
        //start logger:
        Logger logger = LogManager.getLogger(Main.class);
        logger.debug("Logger started");

        //add to resources:
        var resources = new Resources(logger);

        //show in console to start application:
        logger.info("Starting application");

        //show new controller:
        var controller = new MainController(resources);
        controller.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}