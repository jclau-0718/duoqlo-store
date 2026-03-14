module com.duoqlo.duoqlostore {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    exports com.duoqlo.duoqlostore;
//    exports com.duoqlo.duoqlostore.model;
    exports com.duoqlo.duoqlostore.controller;

    opens com.duoqlo.duoqlostore to javafx.fxml;
    opens com.duoqlo.duoqlostore.controller to javafx.fxml;
}