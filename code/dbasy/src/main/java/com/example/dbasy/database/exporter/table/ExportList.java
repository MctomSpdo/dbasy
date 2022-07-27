package com.example.dbasy.database.exporter.table;

import java.util.ArrayList;
import java.util.List;

public class ExportList {
    private static List<TableExporter> tableExporter;

    public static List<TableExporter> getTableExporter() {
        if(tableExporter == null) {
            loadTableExporter();
        }
        return tableExporter;
    }

    private static void loadTableExporter() {
        tableExporter = new ArrayList<>();
        tableExporter.add(new CSVTableExporter());
        tableExporter.add(new ConsoleTableExporter());
        tableExporter.add(new JSONExporter());
    }
}
