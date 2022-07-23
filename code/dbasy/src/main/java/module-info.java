module com.example.dbasy {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.fxmisc.richtext;

    requires java.sql;
    requires reactfx;
    requires org.fxmisc.flowless;

    opens com.example.dbasy to javafx.fxml;
    opens com.example.dbasy.ui to javafx.fxml;
    opens com.example.dbasy.ui.tab.table to javafx.fxml;
    opens com.example.dbasy.ui.tab.code to javafx.fxml;
    opens com.example.dbasy.ui.tab.result to javafx.fxml;
    opens com.example.dbasy.ui.dialogs to javafx.fxml;

    exports com.example.dbasy;

    exports com.example.dbasy.ui;

    exports com.example.dbasy.database;
    exports com.example.dbasy.database.invalid;
    exports com.example.dbasy.database.mysql;
    exports com.example.dbasy.ui.dialogs;
}