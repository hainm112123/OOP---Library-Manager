package org.example.librarymanager.components;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.librarymanager.app.EditDataController;
import org.example.librarymanager.app.LibraryApplication;
import org.example.librarymanager.data.DataAccessObject;
import org.example.librarymanager.models.Model;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataTable<E extends Model> {
    private AnchorPane container;
    private Label title;
    private Button searchBtn;
    private TextField searchBox;
    private Pagination pagination;

    private TableView<List<Pair<String,String>>> table;
    private DataAccessObject dataAccessObject;
    private List<E> originalList;
    private List<E> list;
    private Class<E> clazz;
    private static final int TABLE_WIDTH = 920;
    private static final int TABLE_HEIGHT = 565;
    private static final int TABLE_LIMIT = 13;

    public DataTable(String title, List<E> list, Class<E> clazz, DataAccessObject dataAccessObject) {
        container = new AnchorPane();

        this.title = new Label(title);
        container.getChildren().add(this.title);

        searchBox = new TextField();
        searchBtn = new Button();
        searchBtn.setText("Search");
        searchBtn.setOnAction(event -> searchOnClick());
        container.getChildren().add(searchBtn);
        container.getChildren().add(searchBox);
//        searchBtn.setPrefWidth(50);
//        HBox searchContainer = new HBox();
//        Region spacer = new Region();
//        HBox.setHgrow(spacer, Priority.ALWAYS);
//        searchContainer.getChildren().addAll(spacer, searchBtn, searchBox);
//        container.getChildren().add(searchContainer);

        this.dataAccessObject = dataAccessObject;

        this.originalList = new ArrayList<>(list);
        this.list = new ArrayList<>(originalList);

        this.clazz = clazz;


        table = new TableView();
        table.setEditable(true);
        table.setMinWidth(TABLE_WIDTH);
        table.setPrefWidth(TABLE_WIDTH);
        table.setMaxWidth(TABLE_WIDTH);
        table.setMinHeight(TABLE_HEIGHT);
        table.setPrefHeight(TABLE_HEIGHT);
        table.setMaxHeight(TABLE_HEIGHT);
        addEditColumn();

        List<String> attributes = list.get(0).getAttributes();
        for (int j = 0; j < attributes.size(); j++) {
            String attribute = attributes.get(j);
            TableColumn<List<Pair<String,String>>, String> col = new TableColumn<>(attribute);
            final int columnIndex = j;
            col.setCellValueFactory(cellData -> {
                List<Pair<String, String>> rowPairs = cellData.getValue();
                for (Pair<String, String> pair : rowPairs) {
                    if (pair.getKey().equals(attribute)) {
                        return new SimpleStringProperty(pair.getValue());
                    }
                }
                return new SimpleStringProperty(""); // Return empty if key not found
            });
            col.setPrefWidth(100);

            table.getColumns().add(col);
        }
//        table.setFixedCellSize((TABLE_HEGIHT-65)/TABLE_LIMIT);
        container.getChildren().add(table);

        pagination = new Pagination();
        container.getChildren().add(pagination);

        pagination.setPageCount((this.list.size() - 1) / TABLE_LIMIT + 1);
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            updateTable(newValue.intValue() * TABLE_LIMIT,
                    Math.min(newValue.intValue() * TABLE_LIMIT + TABLE_LIMIT, this.list.size()));
        });

        updateTable(0, Math.min(TABLE_LIMIT, list.size()));

        design();
    }

    void updateTable(int start, int end) {
        this.table.getItems().clear();
        if (start < end) {
//            ObservableList<List<Pair<String,String>>> list = FXCollections.observableArrayList();
            for(int i = start; i < end; i++) {
                List<Pair<String, String>> data = this.list.get(i).getData();
                table.getItems().add(data);
            }
//            table.getItems().addAll(this.list.subList(start, end));
        }
        table.refresh();
    }

    public AnchorPane getContainer() {
        return container;
    }

    private void addEditColumn() {
        String fxmlFile = "edit-data.fxml";
        switch (clazz.getSimpleName()) {
            case "Service":
                return;
            case "User":
                fxmlFile = "edit-data-user.fxml";
                break;
            case "Document":
                fxmlFile = "edit-data-document.fxml";
                break;
            case "Rating":
                fxmlFile = "edit-data-rating.fxml";
                break;
            case "Category":
                fxmlFile = "edit-data-category.fxml";
                break;
            default:
                break;
        }
        TableColumn<List<Pair<String,String>>, Void> actionColumn = new TableColumn<>("");
        String finalFxmlFile = fxmlFile;
        actionColumn.setCellFactory(colCell -> new TableCell<List<Pair<String,String>>, Void>() {
            private final Button button = new Button("Edit");

            {
                button.setOnAction(event -> {
                    try {
                        Stage subStage = new Stage();
                        subStage.initModality(Modality.WINDOW_MODAL);
                        subStage.initOwner(container.getScene().getWindow());
                        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource(finalFxmlFile));
                        Scene subScene = new Scene(fxmlLoader.load());
                        EditDataController<E> controller = fxmlLoader.getController();

                        E tmp = (E) list.get(pagination.getCurrentPageIndex()*TABLE_LIMIT + getTableRow().getIndex());
                        controller.setData(tmp, clazz);
                        subStage.setScene(subScene);
                        subStage.showAndWait();
                        int index = getIndex();
                        table.getItems().set(index, tmp.getData());
                        table.refresh();
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
    }

    private void searchOnClick() {
        String text = searchBox.getText();
        if (text.isEmpty() || text.isBlank()) {
            list.clear();
            for (E data : originalList) {
                list.add(data);
            }
            updateTable(0, Math.min(TABLE_LIMIT, list.size()));
            pagination.setCurrentPageIndex(0);
        } else {
            list.clear();
            for (E data : originalList) {
                List<Pair<String, String>> tmp = data.getData();
                for (Pair<String, String> attribute : tmp) {
                    if (attribute.getValue() != null && attribute.getValue().contains(text)) {
                        list.add(data);
                        break;
                    }
                }
            }
//            System.out.println("Button on click");
//            System.out.println(text);
//            System.out.println(list);
            updateTable(0, Math.min(TABLE_LIMIT, list.size()));
            pagination.setCurrentPageIndex(0);
        }
    }

    private void design() {
        title.setStyle("-fx-font-size: 30");
        title.setLayoutX(23);
        title.setLayoutY(21);

        searchBox.setLayoutX(675);
        searchBox.setLayoutY(57);
        searchBox.setPrefWidth(250);
        searchBox.setPrefHeight(34);

        searchBtn.setLayoutY(57);
        searchBtn.setLayoutX(600);
        searchBtn.setPrefHeight(34);

        AnchorPane.setLeftAnchor(table, 20.0);
        AnchorPane.setRightAnchor(table, 20.0);
        table.setLayoutY(100);

        pagination.setPrefHeight(26);
        pagination.setLayoutX(384);
        pagination.setLayoutY(675);

    }
}
