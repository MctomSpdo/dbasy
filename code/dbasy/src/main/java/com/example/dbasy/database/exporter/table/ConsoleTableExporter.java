package com.example.dbasy.database.exporter.table;

import com.example.dbasy.database.Table;
import com.example.dbasy.database.exporter.ExportException;
import com.example.dbasy.database.exporter.Exporter;
import com.example.dbasy.database.exporter.ExporterRowSplitter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.concurrent.atomic.AtomicInteger;

public class ConsoleTableExporter implements TableExporter {
    private Table table;
    private final Property<String> horizontalSep = new SimpleStringProperty("-");
    private final Property<String> verticalSep = new SimpleStringProperty("|");
    private final Property<String> cornerSep = new SimpleStringProperty("+");

    private final Property<String> padding = new SimpleStringProperty(" ");
    private final Property<ExporterRowSplitter> rowSplitter = new SimpleObjectProperty<>(ExporterRowSplitter.ENTER);
    private final Property<Boolean> includeHeader = new SimpleBooleanProperty(true);

    private final Property<Boolean> rowCount = new SimpleObjectProperty<>(false);

    public ConsoleTableExporter() {
        this.table = null;
    }

    public ConsoleTableExporter(Table table) {
        this.table = table;
    }

    @Override
    public String get() throws ExportException {
        if(this.padding.getValue().length() == 0) {
            return "ERROR: Pattern has to be at least 1 character long";
        }
        if(this.verticalSep.getValue().length() != 1) {
            return "ERROR: VerticalSeparator can only be 1 character long";
        }
        if(this.horizontalSep.getValue().length() != 1) {
            return "ERROR: HorizontalSeparator can only be 1 character long";
        }
        if(this.cornerSep.getValue().length() != 1) {
            return  "ERROR: CornerSeparator can only be 1 character long";
        }
        if(this.table == null) {
            return "ERROR: No Table specified";
        }

        StringBuilder sb = new StringBuilder();

        var header = table.getColumnNames();
        var content = table.getContent();

        var longest = header.stream()
                .map(value -> new AtomicInteger((this.includeHeader.getValue()) ? value.length() : 0))
                .toList();
        //2d array of the length of contents
        content.forEach(value -> {
            for(int i = 0; i < value.size(); i++) {
                var size = value.get(i).length();
                var comparator = longest.get(i);
                if(size > comparator.get()) {
                    comparator.set(size);
                }
            }
        });

        var maxRowCountLength = String.valueOf(content.size()).length();
        var innerContentSize = longest.stream()
                .mapToInt(AtomicInteger::get)
                .sum();
        innerContentSize += longest.size() + longest.size() * padding.getValue().length();
        if(this.rowCount.getValue()) {
            if(maxRowCountLength < 3 && this.includeHeader.getValue()) {
                maxRowCountLength = 3;
            }
            innerContentSize += maxRowCountLength + this.padding.getValue().length() + 2;
        }
        var outerContentSize = innerContentSize + longest.size() + 1; //with borders

        //header
        if(this.includeHeader.getValue()) {
            sb.append(getHorizontalLine(outerContentSize));
            if(this.rowCount.getValue()) {
                sb.append(this.verticalSep.getValue());
                sb.append(this.padding.getValue());
                sb.append("Row");
                sb.append(this.padding.getValue().charAt(0));
            }
            for(int i = 0; i < header.size(); i++) {
                var value = header.get(i);
                var length = longest.get(i).get();
                sb.append(this.verticalSep.getValue());
                sb.append(this.padding.getValue());
                sb.append(value);
                sb.append(String.valueOf(this.padding.getValue().charAt(0)).repeat(length - value.length() + 1));
            }
            sb.append(this.verticalSep.getValue());
            sb.append(this.rowSplitter.getValue().getValue());
        }

        var rowCount = new AtomicInteger(1);

        //content
        sb.append(getHorizontalLine(outerContentSize));
        int finalMaxRowCountLength = maxRowCountLength;
        content.forEach(row -> {
            StringBuilder rowBuilder = new StringBuilder();
            if(this.rowCount.getValue()) {
                var number = rowCount.getAndIncrement();
                var numberLength = String.valueOf(number).length();
                rowBuilder.append(this.verticalSep.getValue());
                rowBuilder.append(this.padding.getValue());
                rowBuilder.append(number);
                rowBuilder.append(String.valueOf(this.padding.getValue().charAt(0)).repeat(finalMaxRowCountLength - numberLength + 1));
            }
            for(int i = 0; i < row.size(); i++) {
                var value = row.get(i);
                var length = longest.get(i).get();
                rowBuilder.append(this.verticalSep.getValue());
                rowBuilder.append(this.padding.getValue());
                rowBuilder.append(value);
                rowBuilder.append(String.valueOf(this.padding.getValue().charAt(0)).repeat(length - value.length() + 1));
            }
            rowBuilder.append(this.verticalSep.getValue());
            sb.append(rowBuilder);
            sb.append(this.rowSplitter.getValue().getValue());
        });
        sb.append(getHorizontalLine(outerContentSize));

        return sb.toString();
    }

    private String getHorizontalLine(int length) {
        return this.cornerSep.getValue() +
                this.horizontalSep.getValue().repeat(length - 2) +
                this.cornerSep.getValue() +
                this.rowSplitter.getValue().getValue();
    }

    @Override
    public Exporter getInstance() {
        return new ConsoleTableExporter();
    }

    @Override
    public VBox getOptionSelectors() {
        var root = new VBox();
        root.setSpacing(5);

        //checkboxes:
        var cbHeader = new CheckBox("Include header");
        cbHeader.selectedProperty().bindBidirectional(this.includeHeader);
        root.getChildren().add(cbHeader);

        var cbRowCount = new CheckBox("RowCount");
        cbRowCount.selectedProperty().bindBidirectional(this.rowCount);
        root.getChildren().add(cbRowCount);

        //textFields
        var gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        gridPane.add(new Label("Horizontal separator"), 0, 0);
        var txtHSep = new TextField();
        txtHSep.textProperty().bindBidirectional(this.horizontalSep);
        gridPane.add(txtHSep, 1, 0);

        gridPane.add(new Label("Vertical separator"), 0, 1);
        var txtVSep = new TextField();
        txtVSep.textProperty().bindBidirectional(this.verticalSep);
        gridPane.add(txtVSep, 1, 1);

        gridPane.add(new Label("Corner character"), 0, 2);
        var txtCSep = new TextField();
        txtCSep.textProperty().bindBidirectional(this.cornerSep);
        gridPane.add(txtCSep, 1, 2);

        gridPane.add(new Label("Padding"), 0, 3);
        var txtPadding = new TextField();
        txtPadding.textProperty().bindBidirectional(this.padding);
        gridPane.add(txtPadding, 1, 3);


        //row Splitter
        gridPane.add(new Label("Row Splitter"), 0, 4);
        var cbRowSplitter = new ChoiceBox<ExporterRowSplitter>();
        cbRowSplitter.valueProperty().bindBidirectional(this.rowSplitter);
        cbRowSplitter.getItems().addAll(ExporterRowSplitter.values());
        gridPane.add(cbRowSplitter, 1, 4);

        root.getChildren().add(gridPane);

        return root;
    }

    @Override
    public String getFileType() {
        return "txt";
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
        return new ConsoleTableExporter(table);
    }

    @Override
    public String toString() {
        return "CONSOLE";
    }
}
