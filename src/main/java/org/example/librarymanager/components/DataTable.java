package org.example.librarymanager.components;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.lang.reflect.*;
import java.util.List;

public class DataTable<E> {
    private VBox container;
    private Label title;
    private MFXScrollPane displayPane;
    private TableView table;
    private List<E> list;
    private Class<E> clazz;
    private Pagination pagination;
    private static final int TABLE_WIDTH = 950;
    private static final int TABLE_HEGIHT = 600;
    private static final int TABLE_LIMIT = 14;

    public DataTable(String title, List<E> list, Class<E> clazz) {
        container = new VBox();

        this.title = new Label(title);
//        this.title.setPrefWidth(TABLE_WIDTH);
        this.title.setPrefHeight(100);
        container.getChildren().add(this.title);

        this.list = list;

        this.clazz = clazz;

        displayPane = new MFXScrollPane();
        displayPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER );
        displayPane.setPrefHeight(650);
        displayPane.setPrefWidth(950);
        container.getChildren().add(displayPane);

        table = new TableView();
        table.setEditable(true);
        table.setMinWidth(TABLE_WIDTH);
        table.setPrefWidth(TABLE_WIDTH);
        table.setMinHeight(TABLE_HEGIHT);
        table.setPrefHeight(TABLE_HEGIHT);
        table.setMaxWidth(TABLE_WIDTH);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            TableColumn<E, Object> col = new TableColumn<>(field.getName());
            col.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
//            col.setPrefWidth(col.getText().length() * 10 + 20);
//            col.setMinWidth(col.getText().length() * 10 + 20);
            table.getColumns().add(col);
        }
//        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        displayPane.setContent(table);


        pagination = new Pagination();
        container.getChildren().add(pagination);

        pagination.setPageCount((this.list.size() - 1) / TABLE_LIMIT + 1);
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            updateTable(newValue.intValue() * TABLE_LIMIT,
                    Math.min(newValue.intValue() * TABLE_LIMIT + TABLE_LIMIT, this.list.size()));
        });

        updateTable(0, Math.min(TABLE_LIMIT, list.size()));
    }

    void updateTable(int start, int end) {
        this.table.getItems().clear();
        if (start < end) {
            table.getItems().addAll(this.list.subList(start, end));
        }
        table.refresh();
    }

    public VBox getContainer() {
        return container;
    }
}
