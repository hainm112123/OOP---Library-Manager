package org.example.librarymanager.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.librarymanager.admin.AddDataController;
import org.example.librarymanager.admin.EditDataController;
import org.example.librarymanager.app.LibraryApplication;
import org.example.librarymanager.data.DataAccessObject;
import org.example.librarymanager.models.*;

import java.util.*;

public class DataTable<E extends Model> {
    private AnchorPane container;
    private Label title;
    private MFXButton searchBtn;
    private MFXTextField searchBox;
    private MFXButton sortBtn;
    private MFXButton addBtn;
    private MFXComboBox<String> sortBox;

    private Pagination pagination;

    private TableView<List<Pair<String,String>>> table;
    private DataAccessObject dataAccessObject;
    private List<E> originalList;
    private List<E> list;
    private Class<E> clazz;
    private E sampleModel;
    private static final int TABLE_WIDTH = 920;
    private static final int TABLE_HEIGHT = 565;
    private static final int TABLE_LIMIT = 13;

    public DataTable(String title, List<E> list, Class<E> clazz, DataAccessObject dataAccessObject) {
        container = new AnchorPane();

        this.title = new Label(title);
        container.getChildren().add(this.title);

        searchBox = new MFXTextField();
        searchBtn = new MFXButton();
        searchBtn.setText("Search");
        searchBtn.setOnAction(event -> searchOnClick());
        container.getChildren().add(searchBtn);
        container.getChildren().add(searchBox);


        this.dataAccessObject = dataAccessObject;

        this.originalList = new ArrayList<>(list);
        this.list = new ArrayList<>(originalList);

        this.clazz = clazz;
        switch (clazz.getSimpleName()) {
            case "User" : {
                sampleModel = (E) new User();
                break;
            }
            case "Category" : {
                sampleModel = (E) new Category();
                break;
            }
            case "Document" : {
                sampleModel = (E) new Document();
                break;
            }
            case "Rating" : {
                sampleModel = (E) new Rating();
                break;
            }
            case "Service" : {
                sampleModel = (E) new Service();
                break;
            }
            default : {
                break;
            }
        }

        table = new TableView();
        table.setEditable(true);
        addEditColumn();

        List<String> attributes = sampleModel.getAttributes();
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


        sortBtn = new MFXButton();
        sortBtn.setText("Sort by");
        sortBtn.setOnAction(event -> sortOnClick());
        container.getChildren().add(sortBtn);
        sortBox = new MFXComboBox<>();
        sortBox.getItems().addAll(attributes);
        container.getChildren().add(sortBox);

        addBtn = new MFXButton();
        addBtn.setText("Add");
        addBtn.setOnAction(event -> addOnClick());
        if (clazz.getSimpleName().equals("Category")) container.getChildren().add(addBtn);

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
            MFXButton button = new MFXButton("Edit");
            {
                button.getStyleClass().add("form-primary-button");
                button.setOnAction(event -> {
                    try {
                        Stage subStage = new Stage();
                        subStage.initModality(Modality.WINDOW_MODAL);
                        subStage.initOwner(container.getScene().getWindow());
                        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource(finalFxmlFile));
                        Scene subScene = new Scene(fxmlLoader.load());
                        EditDataController<E> controller = fxmlLoader.getController();
                        Boolean[] isDel = {Boolean.FALSE};
                        E tmp = (E) list.get(pagination.getCurrentPageIndex()*TABLE_LIMIT + getTableRow().getIndex());
                        controller.setData(tmp, clazz, isDel);
                        subStage.setScene(subScene);
                        subStage.showAndWait();
                        int index = getIndex();
                        table.getItems().set(index, tmp.getData());
                        if (Boolean.TRUE.equals(isDel[0])) {
                            originalList.remove(tmp);
                            list.clear();
                            for (E data : originalList) {
                                list.add(data);
                            }
                            updateTable(0, Math.min(TABLE_LIMIT, list.size()));

                            pagination.setPageCount((list.size() - 1) / TABLE_LIMIT + 1);
                            pagination.setCurrentPageIndex(0);
                        }
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
        sortBox.setValue(null);
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
            updateTable(0, Math.min(TABLE_LIMIT, list.size()));
            pagination.setCurrentPageIndex(0);
        }
    }

    private void sortOnClick() {
        String tmp = sortBox.getValue();
        if (tmp == null) {
            return;
        }
        if (tmp.isBlank() || tmp.isEmpty()) {
            return;
        }

        Collections.sort(list, new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                if (tmp == "ID") {
                    List<Pair<String, String>> tmp1 = o1.getData();
                    List<Pair<String, String>> tmp2 = o2.getData();
                    for (int i = 0; i < tmp1.size(); i++) {
                        if (tmp1.get(i).getKey().equals(tmp)) {
                            Integer x = Integer.parseInt(tmp1.get(i).getValue());
                            Integer y = Integer.parseInt(tmp2.get(i).getValue());
                            return x.compareTo(y);
//                            return tmp1.get(i).getValue().compareTo(tmp2.get(i).getValue());
                        }
                    }
                    return 0;
                } else {
                    List<Pair<String, String>> tmp1 = o1.getData();
                    List<Pair<String, String>> tmp2 = o2.getData();
                    for (int i = 0; i < tmp1.size(); i++) {
                        if (tmp1.get(i).getKey().equals(tmp)) {
                            return tmp1.get(i).getValue().compareTo(tmp2.get(i).getValue());
                        }
                    }
                    return 0;
                }
            }
        });
        updateTable(0, Math.min(TABLE_LIMIT, list.size()));
        pagination.setCurrentPageIndex(0);
    }

    private void addOnClick() {
        try {
            Stage subStage = new Stage();
            E dataEntity;
            Boolean[] isAdd = {Boolean.FALSE};
            switch (clazz.getSimpleName()) {
                case "Category":{
                    Category tmp = new Category();
                    dataEntity = (E) tmp;
                    break;
                }
                default: {
                    dataEntity = null;
                    break;
                }
            }
            subStage.initModality(Modality.WINDOW_MODAL);
            subStage.initOwner(container.getScene().getWindow());
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("add-data.fxml"));
            Scene subScene = new Scene(fxmlLoader.load());
            AddDataController<E> controller = fxmlLoader.getController();
            controller.setData(dataEntity, clazz, isAdd);

            subStage.setScene(subScene);
            subStage.showAndWait();

            if (Boolean.TRUE.equals(isAdd[0])) {
                //System.out.println("Yeah");
                originalList.add(dataEntity);
                list.clear();
                for (E data : originalList) {
                    list.add(data);
                }
                updateTable(0, Math.min(TABLE_LIMIT, list.size()));

                pagination.setPageCount((list.size() - 1) / TABLE_LIMIT + 1);
                pagination.setCurrentPageIndex(0);
            }
            table.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void design() {
        //title.setStyle("-fx-font-size: 30");
        container.getStylesheets().add(getClass().getResource("/org/example/librarymanager/css/form.css").toExternalForm());
        container.setPrefWidth(950);
        container.setPrefHeight(720);
        title.setLayoutX(23);
        title.setLayoutY(21);
        this.title.getStyleClass().add("title");


        searchBox.setLayoutX(675);
        searchBox.setLayoutY(57);
        searchBox.setPrefWidth(250);
        searchBox.setPrefHeight(34);
        searchBox.getStyleClass().add("form-text-field");
        searchBox.setFloatMode(FloatMode.DISABLED);

        searchBtn.setLayoutY(57);
        searchBtn.setLayoutX(600);
        searchBtn.setPrefHeight(34);
        searchBtn.getStyleClass().add("form-primary-button");

        sortBtn.setLayoutY(57);
        sortBtn.setLayoutX(380);
        sortBtn.setPrefHeight(34);
        sortBtn.getStyleClass().add("form-primary-button");

        sortBox.setLayoutY(57);
        sortBox.setLayoutX(460);
        sortBox.setPrefHeight(34);
        sortBox.setPrefWidth(100);
        sortBox.setFloatMode(FloatMode.DISABLED);

        AnchorPane.setLeftAnchor(addBtn, 20.0);
        addBtn.setLayoutY(57);
        addBtn.setPrefHeight(34);
        addBtn.setPrefWidth(64);
        addBtn.getStyleClass().add("form-primary-button");

        AnchorPane.setLeftAnchor(table, 20.0);
        AnchorPane.setRightAnchor(table, 20.0);
        table.setLayoutY(100);
        table.setPrefWidth(TABLE_WIDTH);
        table.setPrefHeight(TABLE_HEIGHT);

        pagination.setPrefHeight(26);
        pagination.setLayoutX(384);
        pagination.setLayoutY(675);

    }
}
