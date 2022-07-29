package com.example.dbasy.ui.tab.code;

import com.example.dbasy.Main;
import com.example.dbasy.Resource;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.invalid.InvalidDatabase;
import com.example.dbasy.ui.UiUtil;
import com.example.dbasy.ui.component.AdvancedCodeArea;
import com.example.dbasy.ui.tab.DataBaseTab;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class CodeTab extends Tab implements Resource, DataBaseTab {
    @FXML
    private VBox root;

    @FXML
    private Button btRun;

    @FXML
    private ChoiceBox<Database> cbDatabase;

    private AdvancedCodeArea codeArea;

    private Database source;

    @FXML
    public void initialize() {
        loadCodeArea();
    }

    @FXML
    void onBtRun(ActionEvent event) {
        query();
    }

    public CodeTab(Database db, Scene scene) {
        super("console");
        if(db instanceof InvalidDatabase) {
            throw new IllegalArgumentException("Database has to be Valid");
        }
        this.source = db;
        try {
            init();
        } catch (IOException e) {
            Main.RESOURCES.log.error("Error loading TAB: ", e);
        }
        //add highlight style css to scene:
        //TODO: don't add it every time when constructed
        scene.getStylesheets().add(CodeTab.class.getResource("sql-keywords.css").toExternalForm());
    }

    public Database getSource() {
        return source;
    }

    public void setSource(Database source) {
        if(source instanceof InvalidDatabase) {
            throw new IllegalArgumentException("Database has to be Valid!");
        }
        this.source = source;
        //reload CodeArea:
        stop();
        loadCodeArea();
    }

    private void init() throws IOException {
        var fxmlLoader = new FXMLLoader(CodeTab.class.getResource("codetab.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        setContent(root);

        //add Database selector to ChoiceBox:
        updateCBDatabase();
        cbDatabase.setOnAction(event -> setSource(cbDatabase.getValue()));
    }

    private void loadCodeArea() {
        if(this.codeArea != null) {
            root.getChildren().remove(this.codeArea);
        }
        var pattern = this.source.getUI().getPattern();
        var codeArea = new AdvancedCodeArea(pattern);

        //stop highlighting when closing the tab
        setOnClosed(event -> {
            Main.RESOURCES.resources.remove(this);
            this.stop();
        });

        //onKey listener:
        codeArea.setOnKeyPressed(this::handleKeyInputs);

        this.codeArea = codeArea;
        this.root.getChildren().add(codeArea);
    }

    @Override
    public void check() {
        if(!Main.RESOURCES.connections.contains(this.source)) {//if database is closed
            close();
        } else {
            updateCBDatabase();
        }
    }

    protected void close() {
        try {
            getTabPane().getTabs().remove(this);
        } catch (NullPointerException ignored) {}
        stop();
    }

    private void updateCBDatabase() {
        cbDatabase.getItems().clear();
        Main.RESOURCES.connections.forEach((value) -> cbDatabase.getItems().add(value));
        cbDatabase.getSelectionModel().select(this.source);
    }

    /**
     * Stops all the Resources in the Tab
     */
    @Override
    public void stop() {
        this.codeArea.stop();
    }

    public void handleKeyInputs(KeyEvent event) {
        if(event.isControlDown()) {
            if(event.getCode() == KeyCode.ENTER) {
                query();
            }
        }
    }

    public void query() {
        var sql = getSelectedStatement();
        sql.forEach((value) -> {
            //clean up String:
            value = value.replaceAll("\n", "").replaceAll("\t", "").trim();
            //create requests for every String (in new Thread for efficiency, but wait till every Thread is done
            if(!value.isEmpty()) {
                try {
                    String finalValue = value;
                    var thread = new Thread(() -> {
                        //make database request and show result
                        try {
                            var result = this.source.request(finalValue);
                            Platform.runLater(() -> Main.getController().addResult(result));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    Main.RESOURCES.log.info("Canceled Statements: ", e);
                }
            }
        });
    }

    /**
     * Gets the sql for the statements that are selected
     * @return selected SQL, or sql where the cursor is at
     */
    public List<String> getSelectedStatement() {
        var text = this.codeArea.getSelectedText();
        if(text.isEmpty()) {
            var sql = UiUtil.getSqlStatementFromPosition(this.codeArea.getText(), this.codeArea.getCaretPosition()).replaceAll(";", "");
            return List.of(sql);
        }
        return List.of(text.split(";"));
    }
}
