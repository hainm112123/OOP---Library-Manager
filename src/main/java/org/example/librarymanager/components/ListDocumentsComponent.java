package org.example.librarymanager.components;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.scene.control.Pagination;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.models.Document;

import java.util.List;

public class ListDocumentsComponent {
    private VBox container;
    private GridPane grid;
    private Pagination pagination;
    private MFXScrollPane scrollPane;
    private ControllerWrapper controller;

    public ListDocumentsComponent(List<Document> documents, MFXScrollPane wrapper, ControllerWrapper controller) {
        container = new VBox();
        grid = new GridPane();
        pagination = new Pagination();
        scrollPane = wrapper;
        this.controller = controller;

        container.setPrefWidth(1200);

        pagination.setPageCount((documents.size() - 1) / 20 + 1);
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            setDocumentsGridPane(documents, newIndex.intValue());
            scrollPane.setVvalue(0);
        });
        setDocumentsGridPane(documents, pagination.getCurrentPageIndex());

        container.getChildren().addAll(grid, pagination);
    }

    /**
     * Display documents in one page controlled by pagination.
     * A page has a grid pane, display max 5 column 4 row.
     * @param documents documents to display
     * @param pageIndex index of display page
     */
    private void setDocumentsGridPane(List<Document> documents, int pageIndex) {
        grid.getChildren().clear();
        int start = pageIndex * 20;
        int end = Math.min(start + 20, documents.size());
        int actualItems = end - start;

        int columns = 5;
        int rows = (int) Math.ceil((double) actualItems / columns);
        rows = Math.max(rows, 2);
        grid.setVgap(60);

        int row = 0;
        int column = 0;
        for (int i = start; i < end; ++ i) {
            Document document = documents.get(i);
            VBox doc = new DocumentComponent(document, controller).getElement();
            grid.add(doc, column, row);
            column++;
            if (column == columns) {
                column = 0;
                row++;
            }
        }
        grid.getRowConstraints().clear();
        for (int r = 0; r < rows; r++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(200);
            grid.getRowConstraints().add(rowConstraints);
        }
        grid.getColumnConstraints().clear();
        for (int c = 0; c < columns; ++ c) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(1200 / columns);
            grid.getColumnConstraints().add(columnConstraints);
        }
    }

    public VBox getContainer() {
        return container;
    }
}
