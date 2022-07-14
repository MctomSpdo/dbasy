package com.example.dbasy;

import com.example.dbasy.database.Repository;
import com.example.dbasy.ui.MainController;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Resources {
    public final List<Repository> connections = new ArrayList<>();
    public MainController controller;
    public final Logger log;

    public Resources(Logger log) {
        this.log = log;
    }
}
