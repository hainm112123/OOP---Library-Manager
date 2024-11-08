package org.example.librarymanager.components;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.librarymanager.app.EditDataController;
import org.example.librarymanager.app.LibraryApplication;

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
        TableColumn<E, Void> actionColumn = new TableColumn<>("");
        actionColumn.setCellFactory(colCell -> new TableCell<E, Void>() {
            private final Button button = new Button("Edit");
            {
                button.setOnAction(event -> {
                    try {
                        Stage subStage = new Stage();
                        subStage.initModality(Modality.WINDOW_MODAL);
                        subStage.initOwner(container.getScene().getWindow());
                        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("edit-data.fxml"));
                        Scene subScene = new Scene(fxmlLoader.load());
                        EditDataController<E> controller = fxmlLoader.getController();
                        E tmp = (E) table.getItems().get(getTableRow().getIndex());
                        controller.setData(tmp, clazz);
                        subStage.setScene(subScene);

                        subStage.showAndWait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });
        table.getColumns().add(actionColumn);

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) if(!Modifier.isStatic(field.getModifiers())) {
            TableColumn<E, Object> col = new TableColumn<>(field.getName());
            col.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
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
