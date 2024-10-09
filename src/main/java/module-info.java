module org.example.librarymanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

//    opens org.example.librarymanager to javafx.fxml;
//    exports org.example.librarymanager;
    exports org.example.librarymanager.app;
    opens org.example.librarymanager.app to javafx.fxml;
//    exports org.example.librarymanager.controllers;
//    opens org.example.librarymanager.controllers to javafx.fxml;
}