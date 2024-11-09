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
    requires lucene.core;
    requires lucene.queryparser;

    exports org.example.librarymanager.app;
    opens org.example.librarymanager.app to javafx.fxml;
    opens org.example.librarymanager.models to javafx.base;
}