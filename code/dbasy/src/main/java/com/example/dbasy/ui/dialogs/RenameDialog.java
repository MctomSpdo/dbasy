package com.example.dbasy.ui.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;

public class RenameDialog {

    @FXML
    private Label btText;

    @FXML
    private TextField txtName;

    @FXML
    public void initialize() {
        this.txtName.setText(oldName);
    }

    private String oldName;

    public String showDialog(String labelText, String oldName) throws IOException {
        var dialog = getDialog();
        this.oldName = oldName;
        btText.setText(labelText);
        dialog.showAndWait();
        return dialog.getResult();
    }

    private Dialog<String> getDialog() throws IOException {
        var dialog = new Dialog<String>();

        //create pane:
        var pane = new DialogPane();

        //add Buttons
        var btSave = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        var btCacnel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(btSave, btCacnel);

        pane.getButtonTypes().sorted();

        //load root node from fxml
        var fxmlLoader = new FXMLLoader(RenameDialog.class.getResource("rename-view.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        pane.setContent(root);

        dialog.setDialogPane(pane);

        dialog.setResultConverter((buttonType -> {
            if(buttonType == btCacnel) {
                return null;
            }
            return this.txtName.getText();
        }));

        return dialog;
    }
}
