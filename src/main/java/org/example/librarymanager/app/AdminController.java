package org.example.librarymanager.app;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.example.librarymanager.components.DataTable;
import org.example.librarymanager.data.*;
import org.example.librarymanager.models.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AdminController extends ControllerWrapper {
    @FXML
    VBox root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userButtonOnClick();

    }

    public void userButtonOnClick() {
        root.getChildren().clear();
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<User>> usersFu = executor.submit(() -> UserQuery.getInstance().getAll());
        try {
            List<User> users = usersFu.get();
            DataTable<User> dataTable = new DataTable<>("Users data", users, User.class);
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    public void documentButtonOnClick() {
        root.getChildren().clear();
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<Document>> documentsFu = executor.submit(() -> DocumentQuery.getInstance().getAll());
        try {
            List<Document> documents = documentsFu.get();
            DataTable<Document> dataTable = new DataTable<>("Documents data", documents, Document.class);
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    public void categoryButtonOnClick() {
        root.getChildren().clear();
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<Category>> categoriesFu = executor.submit(() -> CategoryQuery.getInstance().getAll());
        try {
            List<Category> categories = categoriesFu.get();
            DataTable<Category> dataTable = new DataTable<>("Categories data", categories, Category.class);
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ratingButtonOnClick() {
        root.getChildren().clear();
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<Rating>> ratingsFu = executor.submit(() -> RatingQuery.getInstance().getAll());
        try {
            List<Rating> ratings = ratingsFu.get();
            DataTable<Rating> dataTable = new DataTable<>("Ratings data", ratings, Rating.class);
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void serviceButtonOnClick() {
        root.getChildren().clear();
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<Service>> servicesFu = executor.submit(() -> ServiceQuery.getInstance().getAll());
        try {
            List<Service> services = servicesFu.get();
            DataTable<Service> dataTable = new DataTable<>("Services data", services, Service.class);
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
