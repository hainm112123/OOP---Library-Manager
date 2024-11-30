module org.example.librarymanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires static lombok;
    requires org.apache.commons.dbcp2;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires google.api.services.books.v1.rev114;
    requires google.api.client;
    requires org.checkerframework.checker.qual;
    requires java.desktop;
    requires atlantafx.base;
    requires MaterialFX;
    requires org.kordamp.ikonli.antdesignicons;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.apache.lucene.core;
    requires org.apache.lucene.queryparser;
    requires org.apache.lucene.analysis.common;
    requires software.amazon.awssdk.services.s3;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.auth;
    requires scribejava.core;
    requires scribejava.apis;
    requires javafx.web;
    requires org.apache.commons.validator;
    requires org.json;
    requires undertow.core;

    exports org.example.librarymanager.app;
    opens org.example.librarymanager.app to javafx.fxml;
    opens org.example.librarymanager.models to javafx.base;
    exports org.example.librarymanager.admin;
    opens org.example.librarymanager.admin to javafx.fxml;
}