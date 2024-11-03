package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

import javafx.scene.control.Pagination;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.example.librarymanager.components.DocumentComponent;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Rating;

import javax.print.Doc;

import static org.example.librarymanager.data.DocumentQuery.*;

public class CategoriesController extends ControllerWrapper {
    @FXML
    private Label currentCategoryLabel;
    @FXML
    private GridPane currentCategoryPane;
    @FXML
    private Pagination pagination;
    @FXML
    private MFXScrollPane scrollPane;
    /**
     * Display documents in one page controlled by pagination.
     * A page has a grid pane, display max 5 column 4 row.
     * @param documents documents to display
     * @param pageIndex index of display page
     */
    private void setDocumentsGridPane(List<Document> documents, int pageIndex) {
        currentCategoryPane.getChildren().clear();
        int start = pageIndex * 20;
        int end = Math.min(start + 20, documents.size());
        int actualItems = end - start;

        int columns = 5;
        int rows = (int) Math.ceil((double) actualItems / columns);
        currentCategoryPane.setVgap(60);

        int row = 0;
        int column = 0;
        for (int i = start; i < end; ++ i) {
            Document document = documents.get(i);
            VBox doc = new DocumentComponent(document, this).getElement();
            currentCategoryPane.add(doc, column, row);
            column++;
            if (column == columns) {
                column = 0;
                row++;
            }
        }
        currentCategoryPane.getRowConstraints().clear();
        for (int r = 0; r < rows; r++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(200);
            currentCategoryPane.getRowConstraints().add(rowConstraints);
        }


    }

    /**
     * Initialization.
     * Load document list by current category.
     * Display document list.
     * @param location url to category.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentCategoryLabel.setText(ControllerWrapper.getCurrentCategory());
        executor = Executors.newFixedThreadPool(1);
        Future<List<Document>> documentsFu = executor.submit(() -> DocumentQuery.getInstance().getDocumentsByCategory(ControllerWrapper.getCurrentCategory(), 50));
        try {
            List<Document> documents = documentsFu.get();
            pagination.setPageCount((documents.size() - 1) / 20 + 1);
            pagination.setCurrentPageIndex(0);
            pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                setDocumentsGridPane(documents, newIndex.intValue());
                scrollPane.setVvalue(0);
            });
            setDocumentsGridPane(documents, pagination.getCurrentPageIndex());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }




}
