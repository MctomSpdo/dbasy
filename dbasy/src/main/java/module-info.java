module com.mctom.dbasy {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.mctom.dbasy to javafx.fxml;
    exports com.mctom.dbasy;
}