module com.mctom.dbasy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.mctom.dbasy to javafx.fxml;
    exports com.mctom.dbasy;
    exports com.mctom.dbasy.ui;
    exports com.mctom.dbasy.db;
    exports com.mctom.dbasy.ui.connect;
    opens com.mctom.dbasy.ui to javafx.fxml;
}