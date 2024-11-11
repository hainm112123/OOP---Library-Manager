package org.example.librarymanager.app;

import javafx.application.Platform;
import lombok.experimental.PackagePrivate;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.ResourceBundle;

public class EditDataDocumentController extends EditDataController<Document> {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        Platform.runLater(this::enableDelete);
    }

    @Override
    protected void enableDelete() {
        //super.enableDelete();
    }
}
