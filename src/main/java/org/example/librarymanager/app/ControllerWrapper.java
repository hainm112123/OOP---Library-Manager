package org.example.librarymanager.app;

import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Data;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerWrapper implements Initializable {
    protected static Stage stage;

    public static void switchScene(Scene scene) {
        stage.setScene(scene);
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ControllerWrapper.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
