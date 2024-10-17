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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topbarHomeBtn.setOnAction((event) -> topbarButtonOnSwitchScence("home.fxml"));
        topbarCategoryBtn.setOnAction((event) -> topbarButtonOnSwitchScence("profile.fxml"));
        topbarDocModifyBtn.setOnAction((event) -> topbarButtonOnSwitchScence("new-document.fxml"));
        topbarProfileBtn.setOnAction((event) -> topbarButtonOnSwitchScence("profile.fxml"));
    }

    public void topbarButtonOnSwitchScence(String url) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource(url));
            Scene scene = new Scene(fxmlLoader.load());
            switchScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
