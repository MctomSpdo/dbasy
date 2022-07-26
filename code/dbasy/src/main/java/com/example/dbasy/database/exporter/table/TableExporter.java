package com.example.dbasy.database.exporter.table;

import com.example.dbasy.database.Table;
import com.example.dbasy.database.exporter.Exporter;
import javafx.scene.layout.VBox;

public interface TableExporter extends Exporter {
    void setTable(Table table);
    Table getTable();

    TableExporter getInstance(Table table);

    @Override
    String toString();
}
