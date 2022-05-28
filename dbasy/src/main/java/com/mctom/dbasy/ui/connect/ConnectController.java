package com.mctom.dbasy.ui.connect;

import com.mctom.dbasy.db.ConnectionDetails;
import com.mctom.dbasy.db.DBCollecion;
import com.mctom.dbasy.db.Database;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class ConnectController {
    //<editor-fold desc="FXML Variables">
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
    public ChoiceBox<Database> cbDatabase;

    @FXML
    public Button btSave;
    //</editor-fold>

    //<editor-fold desc="Model / View">
    public ConnectView view;
    public ConnectionDetails model = new ConnectionDetails(true);
    //</editor-fold>

    //<editor-fold desc="FXML Methods">
    /**
     * Generates the String for the database
     */
    @FXML
    public void generateUrl() {
        String url = cbDatabase.getValue().getDBString(model);
        this.txtURL.setText(url);
    }

    /**
     * Generates the field in case the user does enter a URL
     */
    @FXML
    public void generateFields() {
        ConnectionDetails generated = cbDatabase.getValue().getDataFromConnectString(txtURL.getText());

        if(generated != null) {
            txtHost.setText(generated.getHost());
            txtPort.setText(generated.getPort());
            txtSID.setText(generated.getDatabase());
        } else {
            txtHost.setText("");
            txtPort.setText("");
            txtSID.setText("");
        }
    }

    /**
     * Tests the database connection, and then gives the output to the OutputField
     */
    @FXML
    public void test() {
        pbTest.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        Database selection = cbDatabase.getValue();
        connectMessage("Testing db connection...");


        (new Thread(() -> {
            Database db = selection.getNewInstance();
            db.setDetails(model.copyInstance());
            try {
                db.connect();
                db.close();
                Platform.runLater(() -> {
                    connectSuccess("Successfull!");
                    pbTest.setProgress(0);
                });
            } catch (SQLException e) {
                Platform.runLater(()-> {
                    connectError(e.getMessage());
                    pbTest.setProgress(0);
                });
            }
        })).start();
    }

    /**
     * When the user picks a new database, update the port to the new default, and then regenerate the URL
     */
    @FXML
    public void cbNewSelection() {
        txtPort.setText(String.valueOf(cbDatabase.getValue().getDefaultPort()));
        generateUrl();
    }
    //</editor-fold>

    /**
     * Shows and shows and waits for the dialog to finish. The result will then be returned
     * @throws IOException if the fxml file can't be found
     */
    public Database showAndWait() throws IOException {
        this.view = new ConnectView(this);
        Dialog<Database> dialog = view.getDialog();
        init();
        dialog.showAndWait();
        return getResult();
    }

    /**
     * Returns the result of the current input in the input window
     * @return Result of input
     */
    public Database getResult() {
        if(model.isInvalid()) {
            return null;
        }
        Database db = cbDatabase.getValue().getNewInstance();
        db.setDetails(model.copyInstance());
        return db;
    }

    private void init() {
        this.cbDatabase.getItems().addAll(DBCollecion.getBaseList());
        Database selected = cbDatabase.getItems().get(0);
        this.cbDatabase.setValue(selected);
        this.txtPort.setText("" + selected.getDefaultPort());
        txtHost.setText("localhost");
        this.model.setInvalid(false);
    }

    //<editor-fold desc="Connection output">
    private void connectError(String message) {
        txtTestOutput.setText(message);
        txtTestOutput.setStyle("-fx-text-inner-color: red;");
    }

    private void connectMessage(String message) {
        txtTestOutput.setText(message);
        txtTestOutput.setStyle("-fx-text-inner-color: black;");
    }

    private void connectSuccess(String message) {
        txtTestOutput.setText(message);
        txtTestOutput.setStyle("-fx-text-inner-color: green;");
    }
    //</editor-fold>
}
