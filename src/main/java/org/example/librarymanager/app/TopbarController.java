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
        topbarHomeBtn.setOnAction((event) -> safeSwitchScene("home.fxml"));
        List<String> categoryList = CategoryQuery.getCategoriesName();
        ObservableList<String> list = FXCollections.observableArrayList(categoryList);
        topbarCategoryBtn.setItems(list);
        topbarCategoryBtn.setOnAction((event) -> {
            ControllerWrapper.setCurrentCategory(topbarCategoryBtn.getValue());
            safeSwitchScene("category.fxml");
        });

        topbarDocModifyBtn.setOnAction((event) -> safeSwitchScene("new-document.fxml"));
        topbarProfileBtn.setOnAction((event) -> safeSwitchScene("profile.fxml"));
        topbarMyDocBtn.setOnAction((event) -> safeSwitchScene("my-documents.fxml"));
    }

}