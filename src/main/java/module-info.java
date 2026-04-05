module com.duoqlo.duoqlostore {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires java.prefs;
    requires jbcrypt;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires java.xml.crypto;

    exports com.duoqlo.duoqlostore;
    opens com.duoqlo.duoqlostore to javafx.fxml;
    exports com.duoqlo.duoqlostore.view;
    opens com.duoqlo.duoqlostore.view to javafx.fxml;
    exports com.duoqlo.duoqlostore.controller;
    opens com.duoqlo.duoqlostore.controller to javafx.fxml;
    exports com.duoqlo.duoqlostore.model;
    opens com.duoqlo.duoqlostore.model to javafx.fxml;
}