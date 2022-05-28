package com.mctom.dbasy.ui.connect;

import com.mctom.dbasy.db.ConnectionDetails;
import com.mctom.dbasy.db.Database;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.IOException;

public class ConnectView {
    ConnectController controller;

    public ConnectView(ConnectController controller) {
        this.controller = controller;
    }

    /**
     * Gets a dialog to display
     *
     * @return Dialog with {@link ConnectionDetails ConnectionDetails} as result
     * @throws IOException thrown if FXML can't be found
     */
    public Dialog<Database> getDialog() throws IOException {
        Dialog<Database> dialog = new Dialog<>();
        dialog.setTitle("Connection");

        //create new pane:
        DialogPane pane = new DialogPane();

        //add Buttons (save, cancel) to pane:
        ButtonType btSave = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().add(btSave);
        ButtonType btCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().add(btCancel);

        pane.getButtonTypes().sorted();

        //get root node from fxml and set in pane:
        FXMLLoader fxmlLoader = new FXMLLoader(ConnectView.class.getResource("connect-dialog.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        Parent root = scene.getRoot();
        pane.setContent(root);

        //bind view
        controller.txtHost.textProperty().bindBidirectional(controller.model.hostProperty());
        controller.txtPort.textProperty().bindBidirectional(controller.model.portProperty());
        controller.txtSID.textProperty().bindBidirectional(controller.model.databaseProperty());
        controller.txtUsername.textProperty().bindBidirectional(controller.model.userProperty());
        controller.pwPassword.textProperty().bindBidirectional(controller.model.passwordProperty());

        //set DialoguePane in dialog:
        dialog.setDialogPane(pane);

        //set ResultConverter:
        dialog.setResultConverter(buttonType -> {
            if (buttonType == btCancel) {
                Database db =  controller.cbDatabase.getValue().getNewInstance();
                db.setDetails(new ConnectionDetails(true));
                return null;
            }
            ConnectionDetails details = new ConnectionDetails(true);
            Database db =  controller.cbDatabase.getValue().getNewInstance();
            db.setDetails(details);
            return db;
        });
        return dialog;
    }
}
