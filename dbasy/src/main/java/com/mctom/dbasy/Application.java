package com.mctom.dbasy;

import com.mctom.dbasy.db.ConnectionDetails;
import com.mctom.dbasy.db.DBCollecion;
import com.mctom.dbasy.db.Database;
import com.mctom.dbasy.ui.MainController;
import com.mctom.dbasy.ui.connect.ConnectController;
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
        DBCollecion.loadBaseList();

        //connectionDialog:
        ConnectController connectController = new ConnectController();
        Database db = connectController.showAndWait();
        System.out.println(db);
        if(db == null) {
            System.out.println("Exiting because of cancel button pressed");
            System.exit(0);
        }
        dbCollection.add(db);

        //start UI:
        MainController controller = new MainController();
        controller.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}