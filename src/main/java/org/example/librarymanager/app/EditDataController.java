package org.example.librarymanager.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import org.example.librarymanager.data.DataAccessObject;
import org.example.librarymanager.data.DatabaseConnection;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.sql.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class EditDataController<E> extends ControllerWrapper {
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

                String dataString = data.toString();
                String[] fields = dataString.split("\n");
                int row = 0;
                for (String field : fields) {
                    String[] comps = field.split(": ");
                    Label key = new Label(comps[0]);
                    TextField value = new TextField(comps[1]);
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

        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        try (Connection connection = databaseConnection.getConnection()) {
            Field[] fields = clazz.getDeclaredFields();
            int fIndex = 0;
            StringBuffer sqlQuery = new StringBuffer();
            if (clazz.getSimpleName().equals("Category")) {
                sqlQuery.append("UPDATE categories");
            } else {
                sqlQuery.append("UPDATE " + clazz.getSimpleName() + "s");
            }
            sqlQuery.append(" SET ");
            for (int index = 0; index < gridPane.getChildren().size(); index += 2) {
                String label = ((Label)gridPane.getChildren().get(index)).getText().toLowerCase();
                String textField = ((TextField)gridPane.getChildren().get(index + 1)).getText().toLowerCase();

                while (fIndex < fields.length && !fields[fIndex].getName().toLowerCase().equals(label)) {
                    fIndex++;
                }

                if (fields[fIndex].getType() != Integer.class && fields[fIndex].getType() != int.class) {
                    sqlQuery.append(label + " = " + "\'" + textField + "\'");
                } else {
                    sqlQuery.append(label + " = " + textField);
                }

//                sqlQuery.append(label + " = " + textField);
                if (index + 2 < gridPane.getChildren().size()) {
                    sqlQuery.append(", ");
                }
            }
            sqlQuery.append(" WHERE id = " + ((TextField) gridPane.getChildren().get(1)).getText() + ";");
            PreparedStatement ps = connection.prepareStatement(sqlQuery.toString());
//            System.out.println(sqlQuery.toString());
            ps.executeUpdate();
            ps.close();
            message.setText("Successfully applied!");
        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Some errors occurred! Please try again!");
        }
    }

    protected void enableDelete() {

    }

    protected void replaceGridPane(int row, int column, Node node) {
        gridPane.getChildren().set(column + row*2, node);
        GridPane.setRowIndex(node, row);
        GridPane.setColumnIndex(node, column);
    }
}
