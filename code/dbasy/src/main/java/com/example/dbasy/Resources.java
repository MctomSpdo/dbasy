package com.example.dbasy;

import com.example.dbasy.database.Database;
import com.example.dbasy.ui.MainController;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Resources {
    public static final List<Database> repoList = new ArrayList<>();

    public final List<Database> connections = new ArrayList<>();
    public MainController controller;
    public final Logger log;

    public Resources(Logger log) {
        this.log = log;
    }
}
