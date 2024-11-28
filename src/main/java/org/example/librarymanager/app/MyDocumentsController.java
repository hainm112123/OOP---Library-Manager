package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.librarymanager.components.ListDocumentsComponent;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyDocumentsController extends ControllerWrapper {
    @FXML
    private MFXScrollPane scrollPane;
    @FXML
    private VBox container;

    /**
     * Display all documents by owner in a grid pane.
     * Executor manages 1 thread to load documents.
     * @param location url to my-document.fxml
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newSingleThreadExecutor();
        Future<List<Document>> future = executor.submit(() -> DocumentQuery.getInstance().getDocumentsByOwner(getUser().getId()));
        executor.shutdown();
        List<Document> documents = new ArrayList<>();
        try {
            documents = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        container.getChildren().add(new ListDocumentsComponent(documents, scrollPane, this, true).getElement());
    }
}
