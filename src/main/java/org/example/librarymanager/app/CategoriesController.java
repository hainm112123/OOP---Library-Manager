package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

import javafx.scene.control.Pagination;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.example.librarymanager.components.DocumentComponent;
import org.example.librarymanager.components.ListDocumentsComponent;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Rating;

import javax.print.Doc;

import static org.example.librarymanager.data.DocumentQuery.*;

public class CategoriesController extends ControllerWrapper {
    @FXML
    private Label currentCategoryLabel;
    @FXML
    private MFXScrollPane scrollPane;

    /**
     * Initialization.
     * Load document list by current category.
     * Display document list.
     * @param location url to category.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentCategoryLabel.setText(ControllerWrapper.getCurrentCategory().getName());
        executor = Executors.newSingleThreadExecutor();
        Future<List<Document>> future = executor.submit(() -> DocumentQuery.getInstance().getDocumentsByCategory(getCurrentCategory().getId()));
        executor.shutdown();
        List<Document> documents = new ArrayList<>();
        try {
            documents = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        VBox container = new VBox();
        StackPane stackPane = new StackPane();
        stackPane.setPrefHeight(30);
        container.getChildren().addAll(stackPane, new ListDocumentsComponent(documents, scrollPane, this).getContainer());
        scrollPane.setContent(container);
    }
}
