package com.example.dbasy.ui.dialogs;

import com.example.dbasy.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;

public class RenameDatabaseDialog {
    @FXML
    private Label btText;

    @FXML
    private TextField txtDBName;

    Database db;

    @FXML
    public void initialize() {
        this.txtDBName.setText(this.db.getDetails().name);
        this.btText.setText("Rename '" + this.db.getDetails().name + "' to: ");
    }

    public RenameDatabaseDialog(Database db) {
        this.db = db;
    }

    public boolean showDialog() throws IOException {
        var dialog = getDialog();
        dialog.showAndWait();
        var result = dialog.getResult();
        if(result != null && !result.equals("")) {
            this.db.setName(result);
            return true;
        }
        return false;
    }

    private Dialog<String> getDialog() throws IOException {
        var dialog = new Dialog<String>();

        //create pane:
        var pane = new DialogPane();

        //add Buttons:
        var btSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        var btCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(btSave, btCancel);

        pane.getButtonTypes().sorted();

        //load root node from fxml
        var fxmlLoader = new FXMLLoader(RenameDatabaseDialog.class.getResource("renameDB-view.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        pane.setContent(root);

        dialog.setDialogPane(pane);

        dialog.setResultConverter((buttonType -> {
            if(buttonType == btCancel) {
                return null;
            }
            return this.txtDBName.getText();
        }));


        return dialog;
    }
}
