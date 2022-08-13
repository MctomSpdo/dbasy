package com.example.dbasy.ui.dialogs;

import com.example.dbasy.Main;
import com.example.dbasy.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;

public class DatabaseSelectDialog {
    @FXML
    private Label btText;

    @FXML
    private ChoiceBox<Database> cbDatabase;

    @FXML
    private void initialize() {
        updateCbDatabase();
    }

    public Database showDialog(String labelText) throws IOException {
        var dialog = getDialog();
        btText.setText(labelText);
        dialog.showAndWait();
        return dialog.getResult();
    }

    private Dialog<Database> getDialog() throws IOException {
        var dialog = new Dialog<Database>();

        //create pane:
        var pane = new DialogPane();

        //add Buttons:
        var btSave = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        var btCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(btSave, btCancel);

        pane.getButtonTypes().sorted();

        //load root node from fxml:
        var fxmlLoader = new FXMLLoader(DatabaseSelectDialog.class.getResource("databaseselect-view.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        pane.setContent(root);

        dialog.setDialogPane(pane);

        dialog.setResultConverter((buttonType -> {
            if(buttonType == btCancel) {
                return null;
            }
            return this.cbDatabase.getValue();
        }));

        return dialog;
    }

    private void updateCbDatabase() {
        var dbList = Main.RESOURCES.connections;
        cbDatabase.getItems().addAll(dbList);
        cbDatabase.getSelectionModel().selectFirst();
    }
}
