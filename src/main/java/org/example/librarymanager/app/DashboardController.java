package org.example.librarymanager.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController extends ControllerWrapper {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void mainMenuButtonOnAction(ActionEvent event) {
//        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource(".fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        switchScene(scene);
//        stage.show();
        System.out.println("mainMenuButtonOnAction");
    }

    public void categoryButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("document-detail.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            switchScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("categoryButtonOnAction");
    }

    public void profileButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("profile.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            switchScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("profileButtonOnAction");
    }

}
