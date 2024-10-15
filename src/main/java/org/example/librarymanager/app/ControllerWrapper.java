package org.example.librarymanager.app;

import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.librarymanager.models.User;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerWrapper implements Initializable {
    protected static Stage stage;
    private static User user;

    public static void switchScene(Scene scene) {
        stage.setScene(scene);
        Rectangle2D rect = Screen.getPrimary().getVisualBounds();
        stage.setX((rect.getWidth() - stage.getWidth()) / 2);
        stage.setY((rect.getHeight() - stage.getHeight()) / 2);
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ControllerWrapper.stage = stage;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        ControllerWrapper.user = user;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
