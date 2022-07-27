package com.example.dbasy.database.exporter;

import javafx.scene.layout.VBox;

public interface Exporter {
    String get() throws ExportException;

    Exporter getInstance();

    VBox getOptionSelectors();

    String getFileType();

    @Override
    String toString();
}
