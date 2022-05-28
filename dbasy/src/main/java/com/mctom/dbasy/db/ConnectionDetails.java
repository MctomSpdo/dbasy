package com.mctom.dbasy.db;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConnectionDetails {
    //<editor-fold desc="Values">
    private final StringProperty host = new SimpleStringProperty();
    private final StringProperty port = new SimpleStringProperty();
    private final StringProperty database = new SimpleStringProperty();
    private final StringProperty user = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private boolean isInvalid = false;
    //</editor-fold>

    public ConnectionDetails(boolean isInvalid) {
        this.isInvalid = isInvalid;
    }

    public ConnectionDetails(String host, String port, String database, String user, String password, boolean isInvalid) {
        setHost(host);
        setPort(port);
        setDatabase(database);
        setUser(user);
        setPassword(password);
        setInvalid(isInvalid);
    }

    public ConnectionDetails(String host, String port, String database) {
        setHost(host);
        setPort(port);
        setDatabase(database);
        setInvalid(true);
    }

    //<editor-fold desc="Getter, Setter and Property">
    public String getHost() {
        return host.get();
    }

    public StringProperty hostProperty() {
        return host;
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public String getPort() {
        return port.get();
    }

    public StringProperty portProperty() {
        return port;
    }

    public void setPort(String port) {
        this.port.set(port);
    }

    public String getDatabase() {
        return database.get();
    }

    public StringProperty databaseProperty() {
        return database;
    }

    public void setDatabase(String database) {
        this.database.set(database);
    }

    public String getUser() {
        return user.get();
    }

    public StringProperty userProperty() {
        return user;
    }

    public void setUser(String user) {
        this.user.set(user);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public void setInvalid(boolean invalid) {
        isInvalid = invalid;
    }
    //</editor-fold>

    public ConnectionDetails copyInstance() {
        return new ConnectionDetails(getHost(), getPort(), getDatabase(), getUser(), getPassword(), isInvalid());
    }

    @Override
    public String toString() {
        return "ConnectionDetails{" +
                "host=" + host +
                ", port=" + port +
                ", database=" + database +
                ", user=" + user +
                ", password=" + password +
                ", isInvalid=" + isInvalid +
                '}';
    }
}
