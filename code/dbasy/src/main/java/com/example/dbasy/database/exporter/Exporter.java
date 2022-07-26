package com.example.dbasy.database.exporter;

import javafx.scene.layout.VBox;

import java.util.List;

public interface Exporter {
    String get() throws ExportException;

    Exporter getInstance();

    VBox getOptionSelectors();

    String getType();

    @Override
    String toString();
}
