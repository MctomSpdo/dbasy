package com.example.dbasy.database.exporter.table;

import com.example.dbasy.database.Table;
import com.example.dbasy.database.exporter.ExportException;
import com.example.dbasy.database.exporter.Exporter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.util.concurrent.atomic.AtomicInteger;

public class XMLExporter implements TableExporter {
    private Table table;
    private final Property<Boolean> addRowHeader = new SimpleObjectProperty<>(false);

    public XMLExporter() {
    }

    public XMLExporter(Table table) {
        this.table = table;
    }

    @Override
    public String get() throws ExportException {
        StringBuilder sb = new StringBuilder();

        var header = table.getColumnNames();
        var content = table.getContent();
        var rowCounter = new AtomicInteger(1);

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<data>\n");

        content.forEach(value -> {
            sb.append("<row>\n");

            if(this.addRowHeader.getValue()) {
                sb.append("  <rownum>").append(rowCounter.getAndIncrement()).append("</rownum>\n");
            }

            for(int i = 0; i < header.size(); i++) {
                var name = header.get(i);
                var data = value.get(i);


                sb.append("  <").append(name).append(">");
                sb.append(data);
                sb.append("</").append(name).append(">\n");
            }
            sb.append("</row>\n");
        });


        sb.append("</data>");
        return sb.toString();
    }

    @Override
    public Exporter getInstance() {
        return new XMLExporter();
    }

    @Override
    public VBox getOptionSelectors() {
        var root = new VBox();

        var cbNumberRows = new CheckBox("Row numbers");
        cbNumberRows.selectedProperty().bindBidirectional(this.addRowHeader);
        root.getChildren().add(cbNumberRows);

        return root;
    }

    @Override
    public String getFileType() {
        return "xml";
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
        return new XMLExporter(table);
    }

    @Override
    public String toString() {
        return "XML";
    }
}
