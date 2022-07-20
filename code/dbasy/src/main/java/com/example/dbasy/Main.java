package com.example.dbasy;

import com.example.dbasy.database.ConnectionDetails;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.mysql.MySQLDatabase;
import com.example.dbasy.ui.MainController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {
    public static Resources RESOURCES;

    private static MainController controller;


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

        //close all resources on close:
        stage.setOnCloseRequest(windowEvent -> {
            //closes resources:
            resources.resources.forEach((resource -> {
                (new Thread(resource::stop)).start();
            }));

            //close connections:
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

        //prepare Databases:
        Database.addDatabases();

        //user Database dialog:
        //UiUtil.connectionDialog(resources);

        try {
            var details = new ConnectionDetails("localhost", 3306, "db", "app", "app");
            var db = new MySQLDatabase();
            db.setDetails(details);
            db.connect();
            resources.connections.add(db);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //show new controller:
        var controller = new MainController(resources);
        Main.controller = controller;
        controller.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    public static MainController getController() {
        return controller;
    }
}