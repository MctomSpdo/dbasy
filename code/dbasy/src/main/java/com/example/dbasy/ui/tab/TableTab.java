package com.example.dbasy.ui.tab;

import com.example.dbasy.Main;
import com.example.dbasy.database.Table;
import com.example.dbasy.ui.IconLoader;
import com.example.dbasy.ui.UiUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

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
    private Table table;

    @FXML
    public void initialize() {
        initCbRows();
        //load table content
        loadContent();
    }

    private TableTab() {
        try {
            init();
        } catch (IOException e) {
            Main.RESOURCES.log.error("Error loading TAB: ", e);
        }
    }

    public TableTab(Table table) {
        this.table = table;
        setText(table.getName());
        try {
            init();
        } catch (IOException e) {
            Main.RESOURCES.log.error("Error loading TAB: ", e);
        }
    }

    public void init() throws IOException {
        var fxmlLoader = new FXMLLoader(TableTab.class.getResource("tabletab.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        setContent(root);
        setGraphic(UiUtil.getSizedImage(IconLoader.getTable()));
    }

    public void loadContent() {
        if (table == null) {
            throw new IllegalArgumentException("There has to be a Table!");
        }
        new Thread(() -> {
            try {
                this.table.load(getRowLimit(), 0);
                //copy list to get around problems (since we are adding the headers)
                var content = new ArrayList<>(this.table.getContent());
                content.add(0, this.table.getHeaders());
                //copy to arraylist for javafx
                ObservableList<String[]> data = FXCollections.observableArrayList();
                content.forEach((value) -> {
                    data.add(value.toArray(new String[0]));
                });
                data.remove(0);//remove titles from data
                Platform.runLater(() -> {
                    TableView<String[]> table = this.tvMain;
                    for (int i = 0; i < content.get(0).size(); i++) {
                        var tc = new TableColumn(content.get(0).get(i));
                        final int colNo = i;
                        tc.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) p -> new SimpleStringProperty((p.getValue()[colNo])));
                        tc.setPrefWidth(90);
                        table.getColumns().add(tc);
                    }
                    table.setItems(data);
                });
            } catch (SQLException e) {
                e.printStackTrace();
                //TODO: Display error instead of table
            }
        }).start();
    }

    /**
     * Initializes the ChoiceBox with its values
     */
    private void initCbRows() {
        this.cbRows.getItems().add("10 rows");
        this.cbRows.getItems().add("50 rows");
        this.cbRows.getItems().add("100 rows");
        this.cbRows.getItems().add("500 rows");
        this.cbRows.getItems().add("1000 rows");
        this.cbRows.getSelectionModel().select(3);
    }

    private int getRowLimit() {
        return Integer.parseInt(((String) this.cbRows.getValue()).split(" ")[0]);
    }
}
