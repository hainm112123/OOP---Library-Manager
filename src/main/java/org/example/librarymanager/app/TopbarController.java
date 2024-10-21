package org.example.librarymanager.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

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
        ObservableList<String> list = FXCollections.observableArrayList("Mathematics", "Technology", "Philosophy" );
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
