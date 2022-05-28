package com.mctom.dbasy;

import com.mctom.dbasy.db.DBCollecion;
import com.mctom.dbasy.ui.MainController;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    public static final String PORGRAMMNAME = "Dbasy";
    private static DBCollecion dbCollection;

    public static DBCollecion getDbCollection() {
        return Application.dbCollection;
    }

    @Override
    public void start(Stage stage) throws IOException {
        //make new collections of Databases:
        Application.dbCollection = new DBCollecion();

        //connectionDialog:

        //start UI:
        MainController controller = new MainController();
        controller.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}