package org.example.librarymanager.app;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LibraryApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Image icon = new Image(getClass().getResourceAsStream("/org/example/librarymanager/image/icon.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Library Manager");
        stage.setResizable(false);

        ControllerWrapper.setStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}