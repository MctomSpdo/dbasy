package com.example.dbasy.ui.dialogs;

import com.example.dbasy.Main;
import com.example.dbasy.database.Table;
import com.example.dbasy.database.exporter.ExportException;
import com.example.dbasy.database.exporter.ExportResult;
import com.example.dbasy.database.exporter.table.ExportList;
import com.example.dbasy.database.exporter.table.TableExporter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.IOException;

public class TableExportDialog {
    @FXML
    private ChoiceBox<TableExporter> cbExporter;

    @FXML
    private TextField txtSource;

    @FXML
    private VBox vbControl;

    @FXML
    private VBox vbPreview;

    @FXML
    private VBox vbCustomContent;

    CodeArea codeArea;
    TableExporter exporter;

    private Table table;

    public TableExportDialog(Table table) {
        this.table = table;
    }

    @FXML
    public void btPressedAction(ActionEvent event) {
        updateView();
    }

    @FXML
    void onExporterSelection(ActionEvent event) {
        loadExporterOptions();
    }

    @FXML
    public void initialize() {
        //set source:
        this.txtSource.setText(this.table.getSource().getDetails().name + "." + this.table.getName());

        //fill choiceBox:
        this.cbExporter.getItems().addAll(ExportList.getTableExporter());
        this.cbExporter.getSelectionModel().selectFirst();
        loadExporterOptions();

        //codeArea for displaying output:
        this.codeArea = new CodeArea();
        codeArea.setWrapText(true);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setEditable(false);
        codeArea.setAutoHeight(true);

        vbPreview.getChildren().add(codeArea);

        updateView();
    }

    public ExportResult showDialog() throws IOException {
        var dialog = getDialog();
        dialog.showAndWait();
        return dialog.getResult();
    }

    private Dialog<ExportResult> getDialog() throws IOException {
        var dialog = new Dialog<ExportResult>();
        dialog.setResizable(true);

        //create pane:
        var pane = new DialogPane();

        //add buttons:
        var btOk = new ButtonType("Save to File", ButtonBar.ButtonData.OK_DONE);
        var btCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().addAll(btOk, btCancel);
        pane.getButtonTypes().sorted();

        //load root node from fxml:
        var fxmlLoader = new FXMLLoader(TableExportDialog.class.getResource("export-table-view.fxml"));
        fxmlLoader.setController(this);
        var scene = new Scene(fxmlLoader.load());
        var root = scene.getRoot();
        pane.setContent(root);

        dialog.setDialogPane(pane);
        dialog.setTitle("Export Table");

        dialog.setResultConverter(buttonType -> {
            if(buttonType == btOk) {
                try {
                    var text = this.exporter.get();
                    return new ExportResult(exporter, text);
                } catch (ExportException e) {
                    Main.RESOURCES.log.error("Could not export your configuration");
                }
            }
            return null;
        });

        return dialog;
    }

    public void updateView() {
        (new Thread(() -> {
            try {
                var text = exporter.get();
                if(text != null) {
                    Platform.runLater(() -> {
                        codeArea.clear();
                        codeArea.insertText(0, text);
                    });
                }
            } catch (ExportException e) {
                //TODO show error dialog
                e.printStackTrace();
            }
        })).start();
    }

    private void loadExporterOptions() {
        this.vbCustomContent.getChildren().clear();
        exporter = this.cbExporter.getValue().getInstance(table);
        var graphic = exporter.getOptionSelectors();
        this.vbCustomContent.getChildren().add(graphic);
    }
}
