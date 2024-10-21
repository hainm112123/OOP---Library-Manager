package org.example.librarymanager.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.models.Category;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static org.example.librarymanager.data.CategoryQuery.getCategoriesName;

public class TopbarController extends ControllerWrapper {
    @FXML
    Button topbarHomeBtn;
    @FXML
    private ComboBox<String> topbarCategoryBtn;
    @FXML
    Button topbarDocModifyBtn;
    @FXML
    Button topbarProfileBtn;
    @FXML
    Button topbarMyDocBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topbarHomeBtn.setOnAction((event) -> switchScene("home.fxml"));
        List<String> categoryList = CategoryQuery.getCategoriesName();
        for (String categoryName : categoryList) {
            System.out.println(categoryName);
        }
        ObservableList<String> list = FXCollections.observableArrayList(categoryList);
        topbarCategoryBtn.setItems(list);
        topbarCategoryBtn.setOnAction((event) -> {
            ControllerWrapper.setCurrentCategory(topbarCategoryBtn.getValue());
            switchScene("category.fxml");
        });

        topbarDocModifyBtn.setOnAction((event) -> switchScene("new-document.fxml"));
        topbarProfileBtn.setOnAction((event) -> switchScene("profile.fxml"));
        topbarMyDocBtn.setOnAction((event) -> switchScene("my-documents.fxml"));
    }

}
