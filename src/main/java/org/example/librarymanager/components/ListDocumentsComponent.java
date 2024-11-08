package org.example.librarymanager.components;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.models.Document;

import java.util.List;

public class ListDocumentsComponent {
    private static final int DOCUMENTS_PER_PAGE = 10;

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

        container.setPrefWidth(1185);

        pagination.setPageCount((documents.size() - 1) / DOCUMENTS_PER_PAGE + 1);
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            setDocumentsGridPane(documents, newIndex.intValue());
            scrollPane.setVvalue(0);
        });
        setDocumentsGridPane(documents, pagination.getCurrentPageIndex());

        container.getChildren().addAll(grid);
        if (documents.size() > DOCUMENTS_PER_PAGE) {
            container.getChildren().add(pagination);
        }
    }

    /**
     * Display documents in one page controlled by pagination.
     * A page has a grid pane, display max 5 column 4 row.
     * @param documents documents to display
     * @param pageIndex index of display page
     */
    private void setDocumentsGridPane(List<Document> documents, int pageIndex) {
        grid.getChildren().clear();
        int start = pageIndex * DOCUMENTS_PER_PAGE;
        int end = Math.min(start + DOCUMENTS_PER_PAGE, documents.size());
        int actualItems = end - start;

        int columns = 5;
        int rows = (int) Math.ceil((double) actualItems / columns);
        rows = Math.max(rows, 2);
        grid.setVgap(60);

        int row = 0;
        int column = 0;
        for (int i = start; i < end; ++ i) {
            Document document = documents.get(i);
            Node doc = new DocumentComponent(document, controller).getElement();
            grid.add(doc, column, row);
            GridPane.setFillWidth(doc, false);
            column++;
            if (column == columns) {
                column = 0;
                row++;
            }
        }
        grid.getRowConstraints().clear();
        for (int r = 0; r < rows; r++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(DocumentComponent.DOC_COMPONENT_HEIGHT);
            grid.getRowConstraints().add(rowConstraints);
        }
        grid.getColumnConstraints().clear();
        for (int c = 0; c < columns; ++ c) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(1185 / columns);
            columnConstraints.setHalignment(HPos.CENTER);
            grid.getColumnConstraints().add(columnConstraints);
        }
        GridPane.setHalignment(grid, HPos.CENTER);
    }

    public VBox getContainer() {
        return container;
    }
}
