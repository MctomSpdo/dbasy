package com.example.dbasy.database.exporter.table;

import com.example.dbasy.database.Table;
import com.example.dbasy.database.exporter.ExportException;
import com.example.dbasy.database.exporter.Exporter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CSVTableExporter implements TableExporter {
    private Table table;
    private BooleanProperty addColumnHeader = new SimpleBooleanProperty(false);
    private BooleanProperty addRowHeader = new SimpleBooleanProperty(false);
    private BooleanProperty transpose = new SimpleBooleanProperty(false);
    private Property<String> setSplitter = new SimpleStringProperty(",");
    private Property<String> rowSplitter = new SimpleStringProperty("\n");

    public CSVTableExporter() {
    }

    public CSVTableExporter(Table table) {
        this.table = table;
    }

    @Override
    public String get() throws ExportException {
        if (!table.isLoaded()) {
            try {
                table.load(1000, 0);
            } catch (SQLException e) {
                throw new ExportException("Could not load Table");
            }
        }
        var data = this.table.getContent();
        if(data.size() == 0) {
            return "";
        }

        AtomicInteger rowCount = new AtomicInteger(1);
        var exported = data.stream()
                .map(columnData -> {
                    var row = columnData.stream()
                            .map(rowData -> rowData + this.setSplitter.getValue())
                            .collect(Collectors.joining());
                    //remove last splitter:
                    row = row.substring(0, row.length() - 1) + this.rowSplitter.getValue();
                    //add rowHeader
                    if(this.addRowHeader.getValue()) {
                        row = rowCount.getAndIncrement() + this.setSplitter.getValue() + row;
                    }
                    return row;
                })
                .collect(Collectors.joining());

        //add columnHeader
        if(this.addColumnHeader.getValue()) {
            var rowHeader = table.getColumnNames();
            var header = rowHeader
                    .stream()
                    .map(values -> values + this.setSplitter.getValue())
                    .collect(Collectors.joining());
            if(this.addRowHeader.getValue()) {
                header = "#" + this.setSplitter.getValue() + header;
            }
            header = header.substring(0, header.length() -1);

            exported = header + this.rowSplitter.getValue() + exported;
        }
        return exported.substring(0, exported.length() -1);
    }

    @Override
    public Exporter getInstance() {
        return new CSVTableExporter();
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public Table getTable() {
        return this.table;
    }

    @Override
    public TableExporter getInstance(Table table) {
        return new CSVTableExporter(table);
    }

    @Override
    public VBox getOptionSelectors() {
        var box = new VBox();
        box.setSpacing(5);

        //checkboxes:
        var transpose = new CheckBox("Transpose");
        transpose.selectedProperty().bindBidirectional(this.transpose);
        var columnHeader = new CheckBox("Column header");
        columnHeader.selectedProperty().bindBidirectional(this.addColumnHeader);
        var rowHeader = new CheckBox("Row header");
        rowHeader.selectedProperty().bindBidirectional(this.addRowHeader);

        box.getChildren().addAll(transpose, columnHeader, rowHeader);

        //textFields
        var gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        gridPane.add(new Label("Field splitter"), 0, 0);
        var txtSplitter = new TextField();
        txtSplitter.textProperty().bindBidirectional(this.setSplitter);
        gridPane.add(txtSplitter, 1, 0);

        gridPane.add(new Label("Row Splitter"), 0, 1);
        var txtRowSplitter = new TextField();
        txtRowSplitter.textProperty().bindBidirectional(this.rowSplitter);
        gridPane.add(txtRowSplitter, 1, 1);

        box.getChildren().add(gridPane);
        return box;
    }

    @Override
    public String getType() {
        return "csv";
    }

    //<editor-fold desc="Settings for export">
    public boolean isAddColumnHeader() {
        return addColumnHeader.get();
    }

    public void setAddColumnHeader(boolean addColumnHeader) {
        this.addColumnHeader.setValue(addColumnHeader);
    }

    public boolean isAddRowHeader() {
        return addRowHeader.get();
    }

    public void setAddRowHeader(boolean addRowHeader) {
        this.addRowHeader.setValue(addRowHeader);
    }

    public boolean isTranspose() {
        return transpose.get();
    }

    public void setTranspose(boolean transpose) {
        this.transpose.setValue(transpose);
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "CSV";
    }
}
