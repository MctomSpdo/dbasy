package com.example.dbasy.ui.tab;

import com.example.dbasy.Main;
import com.example.dbasy.database.Table;
import com.example.dbasy.ui.IconLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class TableTab extends Tab {
    @FXML
    private Button btAdd;

    @FXML
    private Button btReload;

    @FXML
    private Button btRemove;

    @FXML
    private ChoiceBox cbRows;

    @FXML
    private TableView tvMain;
    Table table;

    private TableTab() {
        try {
            init();
        } catch (IOException e) {
            Main.RESOURCES.log.error("Error loading TAB: ", e);
        }
    }

    public TableTab(Table table) {
        this();
        this.table = table;
        setText(table.getName());
    }

    public void init() throws IOException {
        var fxmlLoader = new FXMLLoader(TableTab.class.getResource("tabletab.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        setContent(root);
        setGraphic(new ImageView(IconLoader.getTable()));
    }
}
