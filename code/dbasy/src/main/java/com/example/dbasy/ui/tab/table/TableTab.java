package com.example.dbasy.ui.tab.table;

import com.example.dbasy.Main;
import com.example.dbasy.database.Table;
import com.example.dbasy.ui.IconLoader;
import com.example.dbasy.ui.UiUtil;
import com.example.dbasy.ui.tab.DataBaseTab;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class TableTab extends Tab implements DataBaseTab {
    //<editor-fold desc="FXML Variables">
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
    //</editor-fold>
    private Table table;

    //<editor-fold desc="FXML Functions">
    @FXML
    public void initialize() {
        this.tvMain.setEditable(true);
        initCbRows();
        //load table content
        loadContent();
    }

    @FXML
    void btAddHandler(ActionEvent event) {

    }

    @FXML
    void btCommitHandler(ActionEvent event) {

    }

    @FXML
    void btReloadHandler(ActionEvent event) {
        btReload.setDisable(true);
        loadContent();
    }

    @FXML
    void btRemoveHandler(ActionEvent event) {

    }

    @FXML
    void btRollbackHandler(ActionEvent event) {

    }
    //</editor-fold>

    //<editor-fold desc="Constructor">
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
    //</editor-fold>

    /**
     * Loads the UserInterface from the FXML file
     * @throws IOException if file does not exist (should not be possible)
     */
    private void init() throws IOException {
        var fxmlLoader = new FXMLLoader(TableTab.class.getResource("tabletab.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        setContent(root);
        setGraphic(UiUtil.getSizedImage(IconLoader.getTable()));

        VBox.setVgrow(this.tvMain, Priority.ALWAYS);
    }

    /**
     * Loads the content of the TableView (async)
     */
    public void loadContent() {
        if (table == null) {
            throw new IllegalArgumentException("There has to be a Table!");
        }
        new Thread(() -> {
            Platform.runLater(this::tvMainSetLoading);
            try {
                this.table.load(getRowLimit(), 0);
                //copy list to get around problems (since we are adding the headers)
                var content = new ArrayList<>(this.table.getContent());
                content.add(0, this.table.getAndLoadColumnNames());
                //copy to arraylist for javafx
                ObservableList<String[]> data = FXCollections.observableArrayList();
                content.forEach((value) -> data.add(value.toArray(new String[0])));
                data.remove(0);//remove titles from data
                Platform.runLater(() -> {
                    TableView<String[]> table = this.tvMain;
                    table.setPlaceholder(new Label("No content in Table"));
                    for (int i = 0; i < content.get(0).size(); i++) {
                        var tc = new TableColumn(content.get(0).get(i));
                        final int colNo = i;
                        tc.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) p -> new SimpleStringProperty((p.getValue()[colNo])));
                        tc.setPrefWidth(90);
                        table.getColumns().add(tc);
                    }
                    table.setItems(data);
                    btReload.setDisable(false);
                });
            } catch (SQLException e) {
                Main.RESOURCES.log.error("Error loading TableTab content: ", e);
                tvMainSetError("Error loading Tables: " + e.getMessage());
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

    /**
     * Gets the set RowLimit from the choice-box
     * @return User selected Row limit
     */
    private int getRowLimit() {
        return Integer.parseInt(((String) this.cbRows.getValue()).split(" ")[0]);
    }

    /**
     * sets a loading icon to the TableView
     */
    private void tvMainSetLoading() {
        this.tvMain.getColumns().clear();
        this.tvMain.setPlaceholder(new ProgressIndicator());
    }

    /**
     * sets an errormessage into the TableView
     * @param message errormessage
     */
    private void tvMainSetError(String message) {
        this.tvMain.getColumns().clear();
        var errorLabel = new Label(message);
        errorLabel.setTextFill(Color.color(1, 0, 0));
        this.tvMain.setPlaceholder(errorLabel);
    }

    @Override
    public void check() {
        if(!Main.RESOURCES.connections.contains(this.table.getSource())) {
            close();
        }
    }

    protected void close() {
        try {
            getTabPane().getTabs().remove(this);
        } catch (NullPointerException ignored) {}
    }
}
