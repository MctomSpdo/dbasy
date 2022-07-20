package com.example.dbasy.ui.tab.result;

import com.example.dbasy.Main;
import com.example.dbasy.database.Result;
import com.example.dbasy.database.Table;
import com.example.dbasy.ui.IconLoader;
import com.example.dbasy.ui.UiUtil;
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
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;

public class ResultTab extends Tab {
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

    }

    @FXML
    void btRemoveHandler(ActionEvent event) {

    }

    @FXML
    void btRollbackHandler(ActionEvent event) {

    }
    //</editor-fold>

    Result result;

    public ResultTab(Result result) {
        super("Query Result", UiUtil.getSizedImage(IconLoader.getTable()));
        this.result = result;
        try {
            init();
        } catch (IOException e) {
            Main.RESOURCES.log.error("Error loading TAB: ", e);
        }
    }

    /**
     * Loads the UserInterface from the FXML file
     *
     * @throws IOException if file does not exist (should not be possible)
     */
    private void init() throws IOException {
        var fxmlLoader = new FXMLLoader(ResultTab.class.getResource("resulttab.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        setContent(root);
        setGraphic(UiUtil.getSizedImage(IconLoader.getTable()));
    }

    public void loadContent() {
        //copy list to get around problems (since we are adding the headers)
        var content = new ArrayList<>(this.result.getContent());
        content.add(0, this.result.getColumnNames());
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
    }
}
