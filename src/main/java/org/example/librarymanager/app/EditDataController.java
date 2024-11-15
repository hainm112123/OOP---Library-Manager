package org.example.librarymanager.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.librarymanager.data.DataAccessObject;
import org.example.librarymanager.models.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditDataController<E extends Model> extends ControllerWrapper {
    @FXML
    protected GridPane gridPane;
    @FXML
    Label title;
    @FXML
    Button backBtn;
    @FXML
    Button applyBtn;
    @FXML
    Button deleteBtn;
    @FXML
    Label message;


    protected E data;
    protected Class<E> clazz;
    protected String editableAttribute;
    protected DataAccessObject dataAccessObject;

    public void setData(E data, Class<E> clazz) {
        this.data = data;
        this.clazz = clazz;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            if (clazz != null) {
                title.setText(clazz.getSimpleName());
                gridPane.getChildren().clear();
                gridPane.getRowConstraints().clear();
                gridPane.getColumnConstraints().clear();
//                gridPane.setPadding(new Insets(10, 10, 10, 10));
                gridPane.setVgap(5);
                gridPane.setHgap(5);

                // Thiết lập ColumnConstraints (Căn giữa cột)
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setHgrow(Priority.ALWAYS);
                col1.setHalignment(HPos.CENTER);  // Căn giữa theo chiều ngang

                ColumnConstraints col2 = new ColumnConstraints();
                col2.setHgrow(Priority.ALWAYS);
                col2.setHalignment(HPos.CENTER);  // Căn giữa theo chiều ngang
                gridPane.getColumnConstraints().addAll(col1, col2);

//                String dataString = data.toString();
//                String[] fields = dataString.split("\n");
                List<Pair<String, String>> attributes = data.getData();
                int row = 0;
                for (Pair<String, String> attribute : attributes) {
                    Label key = new Label(attribute.getKey());
                    TextField value = new TextField(attribute.getValue());
                    value.setEditable(false);

                    RowConstraints rowConstraints = new RowConstraints();
                    rowConstraints.setMinHeight(50);
                    rowConstraints.setPrefHeight(50);
                    rowConstraints.setVgrow(Priority.ALWAYS);
                    rowConstraints.setValignment(VPos.CENTER);
                    gridPane.getRowConstraints().add(rowConstraints);

                    gridPane.add(key, 0, row);
                    gridPane.add(value, 1, row);
                    row++;
                }
                backBtn.setOnAction(e -> {
                    Stage stage = (Stage) backBtn.getScene().getWindow();
                    stage.close();
                });
                applyBtn.setOnAction(e -> {
                    message.setText("You do not have permission to perform this action.");
                });
                deleteBtn.setOnAction(e -> {
                    message.setText("You do not have permission to perform this action.");
                });
                switch (clazz.getSimpleName()) {
                    case "User":
                        editableAttribute = "permission";
                        break;
                    case "Document":
                        editableAttribute = "";
                        break;
                    case "Rating":
                        editableAttribute = "";
                        break;
                    case "Service":
                        editableAttribute = "";
                        break;
                    case "Category":
                        editableAttribute = "name,description";
                        break;
                    default:
                        break;
                }
                //if(!editableAttribute.isBlank() || !editableAttribute.isEmpty()) {
                enableEdit();
//                enableApply();
//                enableDelete();
                //}
            }

        });
    }

    protected void enableEdit() {
        for (int index = 0; index < gridPane.getChildren().size(); index += 2) {
            Label label = (Label)gridPane.getChildren().get(index);
            TextField textField = (TextField)gridPane.getChildren().get(index + 1);
            String value = label.getText().toLowerCase();
            if (editableAttribute.contains(value)) {
                textField.setEditable(true);
            }
        }
    }

    protected void enableApply() {
        applyBtn.setOnAction(e -> {
            applyQuery();
        });
    }

    protected void applyQuery() {
        if (dataAccessObject == null) {
            message.setText("DataAccess is not available");
            return;
        }
        E tmp = getCurrentData();

        if (tmp != null && dataAccessObject.update(tmp)) {
            System.out.println(tmp.toString());
            System.out.println(dataAccessObject);
            data.setData(tmp.getData());
            message.setText("Successfully applied!");
        } else {
            message.setText("Some errors occurred! Please try again!");
        }
    }

    protected void enableDelete() {
        deleteBtn.setOnAction(e -> {
            if (dataAccessObject == null) {
                message.setText("DataAccess is not available");
                return;
            }
            if (dataAccessObject.delete(data)) {
                data.setData(null);
                message.setText("Successfully deleted!");
            }
        });
    }

    protected Node getGridPane(int row, int column) {
        for (Node node : gridPane.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);

            if (nodeRow == null) nodeRow = 0;
            if (nodeCol == null) nodeCol = 0;

            if (nodeRow == row && nodeCol == column) {
                return node;
            }
        }
        return null;
    }

    protected void replaceGridPane(int row, int column, Node node) {
        gridPane.getChildren().set(column + row*2, node);
        GridPane.setRowIndex(node, row);
        GridPane.setColumnIndex(node, column);
    }

    protected E getCurrentData() {
        try {
            List<Pair<String, String>> attributes = new ArrayList<>();
            for (int i = 0; i < gridPane.getChildren().size(); i += 2) {
                Pair<String, String> tmp = new Pair<>
                        (getNodeText(gridPane.getChildren().get(i)),
                                getNodeText(gridPane.getChildren().get(i + 1)));
                attributes.add(tmp);
            }
            E tmp = (E) data.clone();
            tmp.setData(attributes);
            return tmp;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getNodeText(Node node) {
        String value = "";
        switch (node.getClass().getSimpleName()) {
            case "TextField":
                value = ((TextField) node).getText();
                break;
            case "ComboBox":
                value = ((ComboBox<?>)node).getValue().toString();
                break;
            case "Label":
                value = ((Label)node).getText();
                break;
            default:
                System.out.println("Unsupported node type: " + node.getClass().getSimpleName());
                break;
        }

        return value;
    }
}
