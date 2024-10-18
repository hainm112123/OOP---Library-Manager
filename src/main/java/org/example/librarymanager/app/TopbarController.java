package org.example.librarymanager.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TopbarController extends ControllerWrapper {
    @FXML
    Button topbarHomeBtn;
    @FXML
    Button topbarCategoryBtn;
    @FXML
    Button topbarDocModifyBtn;
    @FXML
    Button topbarProfileBtn;
    @FXML
    Button topbarMyDocBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topbarHomeBtn.setOnAction((event) -> switchScene("home.fxml"));
        topbarCategoryBtn.setOnAction((event) -> switchScene("profile.fxml"));
        topbarDocModifyBtn.setOnAction((event) -> switchScene("new-document.fxml"));
        topbarProfileBtn.setOnAction((event) -> switchScene("profile.fxml"));
        topbarMyDocBtn.setOnAction((event) -> switchScene("my-documents.fxml"));
    }
}
