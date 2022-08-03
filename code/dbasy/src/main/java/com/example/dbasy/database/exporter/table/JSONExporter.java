package com.example.dbasy.database.exporter.table;

import com.example.dbasy.database.Table;
import com.example.dbasy.database.exporter.ExportException;
import com.example.dbasy.database.exporter.Exporter;
import javafx.scene.layout.VBox;

public class JSONExporter implements TableExporter {
    private Table table;

    public JSONExporter() {
    }

    public JSONExporter(Table table) {
        this.table = table;
    }

    @Override
    public String get() throws ExportException {
        var headers = table.getColumnNames();
        var content = table.getContent();

        StringBuilder sb = new StringBuilder();

        sb.append("[\n");

        content.forEach(value -> {
            sb.append("  {\n");

            for(int i = 0; i < headers.size(); i++) {
                var name = headers.get(i);
                var data = value.get(i).replaceAll("\"", "\\\"");

                sb.append("    \"").append(name).append("\": \"").append(data).append("\"");
                sb.append((i + 1 < headers.size()) ? ",\n" : "\n");
            }

            sb.append("  },\n");
        });
        if(content.size() > 0) {
            sb.setLength(Math.max(sb.length() - 2, 0)); //remove last 2 characters
        }
        sb.append("\n");
        sb.append("]");

        return sb.toString();
    }

    @Override
    public Exporter getInstance() {
        return new JSONExporter();
    }

    @Override
    public VBox getOptionSelectors() {
        var root = new VBox();

        return root;
    }

    @Override
    public String getFileType() {
        return "json";
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
        return new JSONExporter(table);
    }

    @Override
    public String toString() {
        return "JSON";
    }
}
