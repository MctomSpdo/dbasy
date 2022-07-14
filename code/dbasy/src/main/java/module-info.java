module com.example.dbasy {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;


    opens com.example.dbasy to javafx.fxml;
    exports com.example.dbasy;
    exports com.example.dbasy.ui;
    opens com.example.dbasy.ui to javafx.fxml;
}