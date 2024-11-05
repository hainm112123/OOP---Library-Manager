package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.librarymanager.components.ListDocumentsComponent;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BorrowingDocumentController extends ControllerWrapper {
    @FXML
    private MFXScrollPane scrollPane;

    /**
     * Display all documents user currently borrow in a grid pane.
     * Executor manages 1 thread to load documents.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newSingleThreadExecutor();
        Future<List<Document>> future = executor.submit(() -> ServiceQuery.getInstance().getBorrowingDocuments(getUser().getId()));
        executor.shutdown();
        List<Document> documents = new ArrayList<>();
        try {
            documents = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        VBox container = new VBox();
        StackPane stackPane = new StackPane();
        stackPane.setPrefHeight(50);
        container.getChildren().addAll(stackPane, new ListDocumentsComponent(documents, scrollPane, this).getContainer());
        scrollPane.setContent(container);
    }
}
