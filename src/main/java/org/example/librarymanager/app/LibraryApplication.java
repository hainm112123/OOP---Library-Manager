package org.example.librarymanager.app;

import atlantafx.base.theme.PrimerLight;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LibraryApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
//        UserAgentBuilder.builder()
//                .themes(JavaFXThemes.MODENA)
//                .themes(MaterialFXStylesheets.forAssemble(true))
//                .setDeploy(true)
//                .setResolveAssets(true)
//                .build()
//                .setGlobal();
        Image icon = new Image(getClass().getResourceAsStream("/org/example/librarymanager/image/logo_notext.png"));
        stage.getIcons().add(icon);
        stage.setTitle("libify");
        stage.setResizable(false);

        ControllerWrapper.setStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}