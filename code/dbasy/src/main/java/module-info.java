module com.example.dbasy {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    requires java.sql;

    opens com.example.dbasy to javafx.fxml;
    opens com.example.dbasy.ui to javafx.fxml;

    exports com.example.dbasy;

    exports com.example.dbasy.ui;

    exports com.example.dbasy.database;
    exports com.example.dbasy.database.invalid;
    exports com.example.dbasy.database.mysql;
}