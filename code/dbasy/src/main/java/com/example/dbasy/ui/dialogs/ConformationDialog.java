package com.example.dbasy.ui.dialogs;

import com.example.dbasy.ui.IconLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class ConformationDialog {
    private final String text;

    @FXML
    private ImageView iVMain;

    @FXML
    private Label lbText;

    @FXML
    public void initialize() {
        this.lbText.setText(text);
        this.iVMain.setImage(IconLoader.getWarningFull());
    }

    public ConformationDialog(String text) {
        this.text = text;
    }

    public ConformationDialog(String text, Image image) {
        this(text);
        this.iVMain.setImage(image);
    }

    public boolean showDialog() throws IOException {
        var dialog = getDialog();
        dialog.showAndWait();
        return dialog.getResult();
    }

    private Dialog<Boolean> getDialog() throws IOException {
        var dialog = new Dialog<Boolean>();

        //create pane:
        var pane = new DialogPane();

        //add buttons:
        var btOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        var btCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(btOk, btCancel);

        pane.getButtonTypes().sorted();

        //load root node from fxml:
        var fxmlLoader = new FXMLLoader(ConformationDialog.class.getResource("conformation-view.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        pane.setContent(root);

        dialog.setDialogPane(pane);
        dialog.setTitle("Confirm");

        dialog.setResultConverter(buttonType -> buttonType == btOk);

        return dialog;
    }
}
