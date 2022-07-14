package com.example.dbasy.ui;

import com.example.dbasy.Main;
import com.example.dbasy.Resources;
import com.example.dbasy.database.ConnectionDetails;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.invalid.InvalidDatabase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class ConnectController {
    //<editor-fold desc="FXML Vars">
    @FXML
    public TextField txtHost;
    @FXML
    public TextField txtPort;
    @FXML
    public TextField txtSID;
    @FXML
    public TextField txtUsername;
    @FXML
    public PasswordField pwPassword;
    @FXML
    public TextField txtURL;
    @FXML
    public TextField txtTestOutput;
    @FXML
    public ProgressBar pbTest;
    @FXML
    public ChoiceBox<Database> cbDBSelect;

    //Buttons:
    @FXML
    public Button btSave;

    @FXML
    public Button btTest;
    //</editor-fold>

    private Database currentSelection;

    //<editor-fold desc="FXML Methods">
    @FXML
    public void initialize() {
        //prepare Database choice-box:
        this.cbDBSelect.getItems().clear();
        this.cbDBSelect.getItems().addAll(Resources.repoList);
        this.cbDBSelect.getSelectionModel().selectFirst();
        this.currentSelection = this.cbDBSelect.getValue();

        //set text fields:
        this.txtPort.setText(String.valueOf(this.currentSelection.getPort()));

        generateUrl();
    }

    @FXML
    public void generateUrl() {
        this.txtURL.setText(this.currentSelection.getUrl(getCurrentDetails()));
    }

    @FXML
    public void generateFields() {
        //TODO: implement method
    }

    @FXML
    public void test() {
        testConnection();
    }

    @FXML
    public void onCbSelection() {
        Database db = cbDBSelect.getValue();
        //if port wasn't changed by the user:
        if(this.currentSelection != null) {
            if(Integer.parseInt(this.txtPort.getText()) == this.currentSelection.getPort()) {
                this.txtPort.setText(String.valueOf(db.getPort()));
            }
        }
    }
    //</editor-fold>

    public Database showDialog() throws IOException {
        var dialog = getDialog();
        dialog.showAndWait();
        return dialog.getResult();
    }

    /**
     * Gets a dialog to display
     *
     * @return Dialog with {@link ConnectionDetails ConnectionDetails} as result
     * @throws IOException thrown if FXML can't be found
     */
    private Dialog<Database> getDialog() throws IOException {
        Dialog<Database> dialog = new Dialog<>();
        dialog.setTitle("Connection");

        //create new pane:
        var pane = new DialogPane();

        //add Buttons (save, cancel) to pane:
        var btSave = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().add(btSave);
        var btCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().add(btCancel);

        pane.getButtonTypes().sorted();

        //get root node from fxml and set in pane:
        var fxmlLoader = new FXMLLoader(ConnectController.class.getResource("connect-view.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        pane.setContent(root);

        //set DialoguePane in dialog:
        dialog.setDialogPane(pane);


        //set ResultConverter:
        dialog.setResultConverter(buttonType -> {
            if (buttonType == btCancel) {
                return new InvalidDatabase();
            }
            var details = getCurrentDetails();
            var db = currentSelection.getNewInstance();
            db.setDetails(details);
            return db;
        });
        return dialog;
    }

    private ConnectionDetails getCurrentDetails() {
        return new ConnectionDetails(this.txtHost.getText(), Integer.parseInt(this.txtPort.getText()), this.txtSID.getText(), this.txtUsername.getText(), this.pwPassword.getText());
    }

    //<editor-fold desc="Test Connection">

    /**
     * Tests the current connection with the given details, and then sets output text based on it
     */
    private void testConnection() {
        var db = this.currentSelection.getNewInstance();
        db.setDetails(getCurrentDetails());
        (new Thread(() -> {
            Platform.runLater(() -> {
                this.btTest.setDisable(true);
                this.pbTest.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                testText("Testing Connection....");
            });
            Main.RESOURCES.log.info("Testing Connection..");
            try {
                db.connect();
                //if there was no exception, it should work:
                db.close();
                Platform.runLater(() -> {
                    this.pbTest.setProgress(0);
                    testSuccess("Connection Successful!");
                    this.btTest.setDisable(false);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    this.pbTest.setProgress(0);
                    testFailure("Could not connect");
                    this.btTest.setDisable(false);
                });
                Main.RESOURCES.log.debug(e);
            }
        })).start();
    }

    private void testSuccess(String text) {
        this.txtTestOutput.setStyle("-fx-text-fill: green;");
        this.txtTestOutput.setText(text);
    }

    private void testFailure(String text) {
        this.txtTestOutput.setStyle("-fx-text-fill: red;");
        this.txtTestOutput.setText(text);
    }

    private void testText(String text) {
        this.txtTestOutput.setStyle("-fx-text-fill: black;");
        this.txtTestOutput.setText(text);
    }
    //</editor-fold>
}
