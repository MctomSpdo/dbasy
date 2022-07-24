package com.example.dbasy.ui;

import com.example.dbasy.Resources;
import com.example.dbasy.database.Database;
import com.example.dbasy.database.Result;
import com.example.dbasy.database.Table;
import com.example.dbasy.ui.tab.code.CodeTab;
import com.example.dbasy.ui.tab.result.ResultTab;
import com.example.dbasy.ui.tab.table.TableTab;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainController {
    final Resources resources;

    @FXML
    private MenuBar mbMain;

    @FXML
    private VBox root;

    @FXML
    private Tab tabConsole;

    @FXML
    private TabPane tbResults;

    @FXML
    private TreeView trDatabases;

    @FXML
    private TabPane tbMain;

    Scene scene;

    @FXML
    public void initialize() {
        resources.log.debug("Loading Main Display components");

        trDatabases.setShowRoot(false);
        trDatabases.setRoot(new TreeItem());
        refreshDBTree(true);
    }

    /**
     * Connection Menu for adding new Connection
     * @param event ActionEvent
     */
    @FXML
    public void cmConnNewConn(ActionEvent event) {
        UiUtil.connectionDialog(resources);
        refreshDBTree(false);
    }

    /**
     * Refreshes the View (View -> Refresh Tree)
     * @param event ActionEvent
     */
    @FXML
    public void viewRefreshTree(ActionEvent event) {
        refreshDBTree(false);
    }

    @FXML
    public void trMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                var list = trDatabases.getSelectionModel().getSelectedItems();
                if(list.size() > 0) {
                    var item = list.get(0);
                    if(item instanceof TreeItem) {
                        if(((TreeItem<?>) item).getValue() instanceof Table) {
                            Table t = (Table) ((TreeItem<?>) item).getValue();
                            var tab = new TableTab(t);
                            tbMain.getTabs().add(tab);
                            tbMain.getSelectionModel().select(tab);
                        }
                    }
                }
            }
        }
    }

    /**
     * On TreeView Context menu Requested
     *
     * Loads the correct ContextMenu for the selected TreeItem
     * @param event ContextMenuEvent
     */
    @FXML
    void trDatabasesContextHandler(ContextMenuEvent event) {
        List<Object> selectedItems = this.trDatabases.getSelectionModel().getSelectedItems();
        if(selectedItems.size() > 0) {
            Object selector = selectedItems.get(0);

            if(selector instanceof TreeItem<?>) {
                Object type = ((TreeItem<?>) selector).getValue();
                if(type instanceof ContextItem) {
                    this.trDatabases.setContextMenu(((ContextItem) type).getContextMenu());
                    return;
                }
            }
        }
        this.trDatabases.setContextMenu(new ContextMenu());
    }

    public MainController(Resources resources) {
        resources.controller = this;
        this.resources = resources;
    }

    public Scene getScene() {
        return scene;
    }

    public void show(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader(MainController.class.getResource("main-view.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load(), 800, 600);
        this.scene = scene;
        stage.setTitle("DBasy");
        stage.setScene(scene);
        stage.show();

        newCodeTab(resources.connections.get(0));
    }

    /**
     * Adds a new CodeTab to the Main Window
     * @param db Database for the CodeTab
     */
    public void newCodeTab(Database db) {
        CodeTab tab = new CodeTab(db, this.scene);
        tbMain.getTabs().add(tab);
        tbMain.getSelectionModel().select(tab);
    }

    /**
     * Loads the TreeView
     * @param doRefresh if Tables and contents should be refreshed
     */
    public void refreshDBTree(boolean doRefresh) {
        trDatabases.getRoot().getChildren().clear();
        var databases = resources.connections;
        databases.forEach((database -> {
            var treeItem = new TreeItem(database);
            //set values for Database icon:
            treeItem.setGraphic(UiUtil.getSizedImage(database.getUI().getIcon()));
            treeItem.setExpanded(true);
            //set child elements:
            var dbItem = new TreeItem<>("db");
            dbItem.setExpanded(true);
            dbItem.setGraphic(UiUtil.getSizedImage(IconLoader.getDatabase()));
            var serverItem = new TreeItem<>("Server Objects");
            serverItem.setGraphic(UiUtil.getSizedImage(IconLoader.getFolder()));
            var tablesItem = new TreeItem<>("tables");
            tablesItem.setGraphic(UiUtil.getSizedImage(IconLoader.getFolder()));
            tablesItem.getChildren().add(UiUtil.getLoadingTreeItem());
            tablesItem.expandedProperty().addListener((observableValue, aBoolean, t1) -> {
                //if treeItem was opened and first element is Loading...
                if(t1 || doRefresh) {
                    var firstItem = tablesItem.getChildren().get(0);
                    try {
                        if(doRefresh || firstItem == null || firstItem.getValue().equals(UiUtil.getLoadingTreeItem().getValue())) {
                            //load Tables from Database (start new Thread due to database)
                            (new Thread(() -> {
                                try {
                                    var tableList = database.getTables();
                                    //set tables in Tree View
                                    Platform.runLater(() -> {
                                        tablesItem.getChildren().clear();
                                        tableList.forEach((table -> {
                                            var tableItem = new TreeItem(table);
                                            tableItem.setGraphic(UiUtil.getSizedImage(IconLoader.getTable()));
                                            tablesItem.getChildren().add(tableItem);

                                            //children of database table:
                                            var columnsItem = UiUtil.getColumnTreeItem(table);
                                            tableItem.getChildren().add(columnsItem);
                                        }));
                                    });
                                } catch (SQLException e) {
                                    resources.log.error("Could not load Tables from" + database, e);
                                    //add error Tree Item:
                                    Platform.runLater(() -> {
                                        tablesItem.getChildren().add(UiUtil.getErrorTreeItem());
                                    });
                                }
                            })).start();
                        }
                    } catch (ClassCastException ignored) {} //when first element gets changed to table after check and before execution

                }
            });

            dbItem.getChildren().add(tablesItem);

            treeItem.getChildren().add(dbItem);
            treeItem.getChildren().add(serverItem);
            trDatabases.getRoot().getChildren().add(treeItem);
        }));
    }

    /**
     * Adds a Result to the View
     * @param result Result of a Query
     */
    public void addResult(Result result) {
        var tab = new ResultTab(result);
        this.tbResults.getTabs().add(tab);
        this.tbResults.getSelectionModel().select(tab);
    }
}