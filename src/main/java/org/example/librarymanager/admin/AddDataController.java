package org.example.librarymanager.admin;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
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
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DataAccessObject;
import org.example.librarymanager.models.Model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddDataController<E extends Model>  extends ControllerWrapper {
    private static final int LABEL_WIDTH = 150;
    private static final int VALUE_WIDTH = 350;
    @FXML
    protected GridPane gridPane;
    @FXML
    Label title;
    @FXML
    Button backBtn;
    @FXML
    Button addBtn;
    @FXML
    Label message;

    E data;
    Class<E> clazz;
    Boolean[] isAdd;
    protected DataAccessObject dataAccessObject;

    public void setData(E data, Class<E> clazz, Boolean[] isAdd) {
        this.data = data;
        this.clazz = clazz;
        this.isAdd = isAdd;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            if (clazz != null) {
                title.getParent().getStylesheets().add(getClass().getResource("/org/example/librarymanager/css/form.css").toExternalForm());
                title.setText(clazz.getSimpleName());
                title.getStyleClass().add("form-title");
                gridPane.getChildren().clear();
                gridPane.getRowConstraints().clear();
                gridPane.getColumnConstraints().clear();
//                gridPane.setPadding(new Insets(10, 10, 10, 10));
                gridPane.setVgap(5);
                gridPane.setHgap(5);


                ColumnConstraints col1 = new ColumnConstraints();
//                col1.setHgrow(Priority.ALWAYS);
                col1.setMinWidth(LABEL_WIDTH);
                col1.setPrefWidth(LABEL_WIDTH);
                col1.setMaxWidth(LABEL_WIDTH);
                col1.setHalignment(HPos.LEFT);

                ColumnConstraints col2 = new ColumnConstraints();
//                col2.setHgrow(Priority.ALWAYS);
                col2.setMinWidth(VALUE_WIDTH);
                col2.setPrefWidth(VALUE_WIDTH);
                col2.setMaxWidth(VALUE_WIDTH);
                col2.setHalignment(HPos.LEFT);
                gridPane.getColumnConstraints().addAll(col1, col2);


                List<Pair<String, String>> attributes = data.getData();
                int row = 0;
                for (Pair<String, String> attribute : attributes) {
                    if (attribute.getKey().equals("ID")) {
                        continue;
                    }
                    Label key = new Label(attribute.getKey());
                    key.setPrefWidth(LABEL_WIDTH);
                    key.setMinWidth(LABEL_WIDTH);
                    key.setMaxWidth(LABEL_WIDTH);
                    key.getStyleClass().add("form-label");
                    MFXTextField value = new MFXTextField(attribute.getValue());
//                    value.setEditable(false);
                    value.getStyleClass().add("data-text-field");
                    value.setFloatMode(FloatMode.DISABLED);
                    value.setMinWidth(VALUE_WIDTH);
                    value.setPrefWidth(VALUE_WIDTH);
                    value.setMaxWidth(VALUE_WIDTH);

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
                switch (clazz.getSimpleName()) {
                    case "Category": {
                        dataAccessObject = CategoryQuery.getInstance();
                        break;
                    }
                    default: {
                        dataAccessObject = null;
                        break;
                    }
                }
                addBtn.setOnAction(e -> applyQuery());
            }
        });
    }

    protected void applyQuery() {
        if (dataAccessObject == null) {
            message.setText("DataAccess is not available");
            return;
        }
        E tmp = getCurrentData();

        if (tmp != null ) {
            tmp = (E) dataAccessObject.add(tmp);
            if (tmp != null) {
                System.out.println(tmp.toString());
                System.out.println(dataAccessObject);
                isAdd[0] = Boolean.TRUE;
                data.setData(tmp.getData());
                message.setText("Successfully added");
            }
        } else {
            message.setText("Some errors occurred! Please try again!");
        }
    }

    protected E getCurrentData() {
        try {
            List<Pair<String, String>> attributes = new ArrayList<>();
            attributes.add(new Pair<String, String>("ID", "0"));
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
            case "MFXTextField":
                value = ((MFXTextField) node).getText();
                break;
            case "MFXComboBox":
                value = ((MFXComboBox<?>)node).getValue().toString();
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
