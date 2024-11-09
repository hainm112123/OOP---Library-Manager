package org.example.librarymanager.components;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.geometry.HPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import lombok.Data;
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.models.Document;

import java.util.List;

@Data
public class ListDocumentsComponent {
    private static final int DOCUMENTS_PER_PAGE = 10;

    private VBox container;
    private GridPane grid;
    private Pagination pagination;
    private MFXScrollPane scrollPane;
    private ControllerWrapper controller;
    private HBox btnGroup;
    private Label listViewBtn;
    private Label gridViewBtn;

    private ListDocumentsSubject subject;
    private ListDocumentsObserver observer;

    public ListDocumentsComponent(List<Document> documents, MFXScrollPane wrapper, ControllerWrapper controller) {
        container = new VBox();
        grid = new GridPane();
        pagination = new Pagination();
        scrollPane = wrapper;
        this.controller = controller;
        btnGroup = new HBox();
        FontAwesomeIconView listIcon = new FontAwesomeIconView(FontAwesomeIcon.LIST_UL, "24");
        FontAwesomeIconView gridIcon = new FontAwesomeIconView(FontAwesomeIcon.TH, "24");
        listViewBtn = new Label("", listIcon);
        gridViewBtn = new Label("", gridIcon);
        gridIcon.setFill(Paint.valueOf("#fff"));
        gridViewBtn.getStyleClass().add("view-type-btn--active");

        subject = new ListDocumentsSubject(gridViewBtn, listViewBtn, gridIcon, listIcon);
        observer = new ListDocumentsObserver(documents, this);
        subject.attach(observer);

        container.setPrefWidth(1185);
        pagination.setPageCount((documents.size() - 1) / DOCUMENTS_PER_PAGE + 1);
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            setDocumentsGridPane(documents, newIndex.intValue());
            scrollPane.setVvalue(0);
        });
        setDocumentsGridPane(documents, pagination.getCurrentPageIndex());

        gridViewBtn.setOnMouseClicked(e -> subject.changeDisplayType(DocumentComponent.VIEW_TYPE_GRID));
        listViewBtn.setOnMouseClicked(e -> subject.changeDisplayType(DocumentComponent.VIEW_TYPE_LIST));

        btnGroup.getChildren().addAll(listViewBtn, gridViewBtn);
        btnGroup.getStyleClass().add("btn-group");
        listViewBtn.getStyleClass().add("view-type-btn");
        gridViewBtn.getStyleClass().add("view-type-btn");
        container.getChildren().addAll(btnGroup, grid);
        container.getStylesheets().add(getClass().getResource("/org/example/librarymanager/css/list-document.css").toExternalForm());
        if (documents.size() > DOCUMENTS_PER_PAGE) {
            container.getChildren().add(pagination);
            pagination.setCursor(Cursor.HAND);
        }
    }

    /**
     * Display documents in one page controlled by pagination.
     * A page has a grid pane, display max 5 column 4 row.
     * @param documents documents to display
     * @param pageIndex index of display page
     */
    public void setDocumentsGridPane(List<Document> documents, int pageIndex) {
        grid.getChildren().clear();
        int start = pageIndex * DOCUMENTS_PER_PAGE;
        int end = Math.min(start + DOCUMENTS_PER_PAGE, documents.size());
        int actualItems = end - start;

        int columns = subject.getDisplayType() == DocumentComponent.VIEW_TYPE_LIST ? 2 :  5;
        int rows = (int) Math.ceil((double) actualItems / columns);
        grid.setVgap(60);

        int row = 0;
        int column = 0;
        for (int i = start; i < end; ++ i) {
            Document document = documents.get(i);
            Node doc = new DocumentComponent(document, controller, subject.getDisplayType()).getElement();
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
            if (subject.getDisplayType() == DocumentComponent.VIEW_TYPE_LIST) {
                rowConstraints.setPrefHeight(DocumentComponent.DOC_COMPONENT_HEIGHT_LIST);
            } else {
                rowConstraints.setPrefHeight(DocumentComponent.DOC_COMPONENT_HEIGHT_GRID);
            }
            grid.getRowConstraints().add(rowConstraints);
        }
        grid.getColumnConstraints().clear();
        for (int c = 0; c < columns; ++ c) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(1185 / columns);
            if (subject.getDisplayType() == DocumentComponent.VIEW_TYPE_LIST) {
                columnConstraints.setHalignment(HPos.CENTER);
            } else {
                columnConstraints.setHalignment(HPos.CENTER);
            }
            grid.getColumnConstraints().add(columnConstraints);
        }
    }

    public Node getElement() {
        return container;
    }
}
