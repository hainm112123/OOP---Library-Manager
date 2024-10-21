package org.example.librarymanager.app;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

public class ControllerWrapper implements Initializable {
    protected static Stage stage;
    private static User user;
    private static Document currentDocument;
    private static List<String> urls = new ArrayList<>();
    private static String currentCategory = "";
    protected ExecutorService executor;

    static {
        urls = new ArrayList<>();
        urls.add("home.fxml");
    }

    private static void unsafeSwitchScene(String url) {
        try {
            urls.add(url);
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource(url));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            Rectangle2D rect = Screen.getPrimary().getVisualBounds();
            stage.setX((rect.getWidth() - stage.getWidth()) / 2);
            stage.setY((rect.getHeight() - stage.getHeight()) / 2);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void safeSwitchScene(String url) {
        unsafeSwitchScene(url);
        if (executor != null) executor.shutdownNow();
    }

    public void backScene() {
        urls.removeLast();
        safeSwitchScene(urls.getLast());
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

    public static Document getCurrentDocument() {
        return currentDocument;
    }

    public static void setCurrentDocument(Document currentDocument) {
        ControllerWrapper.currentDocument = currentDocument;
    }

    public static String getCurrentCategory() {
        return currentCategory;
    }

    public static void setCurrentCategory(String currentCategory) {
        ControllerWrapper.currentCategory = currentCategory;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
