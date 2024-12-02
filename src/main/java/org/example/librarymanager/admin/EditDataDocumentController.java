package org.example.librarymanager.admin;

import javafx.application.Platform;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.ResourceBundle;

public class EditDataDocumentController extends EditDataController<Document> {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        dataAccessObject = DocumentQuery.getInstance();
//        Platform.runLater(this::enableDelete);
    }

//    @Override
//    protected void enableDelete() {
//        super.enableDelete();
//    }
}
