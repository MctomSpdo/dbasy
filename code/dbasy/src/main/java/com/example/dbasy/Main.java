package com.example.dbasy;

import com.example.dbasy.database.Database;
import com.example.dbasy.database.invalid.InvalidDatabase;
import com.example.dbasy.ui.ConnectController;
import com.example.dbasy.ui.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {
    public static Resources RESOURCES;
    @Override
    public void start(Stage stage) throws IOException {
        //start logger:
        var logger = LogManager.getLogger(Main.class);
        logger.debug("Logger started");

        //add to resources:
        var resources = new Resources(logger);
        Main.RESOURCES = resources;

        //show in console to start application:
        logger.info("Starting application");

        //prepare Databases:
        Database.addDatabases();

        //user Database dialog:
        var connectController = new ConnectController();
        var db = connectController.showDialog();
        if(!(db instanceof InvalidDatabase)) {
            resources.connections.add(db);
            try {
                db.connect();
            } catch (SQLException e) {
                logger.info("Default connection failed: ", e);
            }
        }

        //close all resources on close:
        stage.setOnCloseRequest(windowEvent -> {
            var connections = resources.connections;
            connections.forEach((database) -> {
                (new Thread(() -> {
                    try {
                        database.close();
                    } catch (SQLException e) {
                        resources.log.error("Couldn't close db: ", e);
                    }
                })).start();
            });
        });

        //show new controller:
        var controller = new MainController(resources);
        controller.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}