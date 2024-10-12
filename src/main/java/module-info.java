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

    exports org.example.librarymanager.app;
    opens org.example.librarymanager.app to javafx.fxml;
}