package org.example.librarymanager.admin;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.example.librarymanager.Common;
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.components.DataTable;
import org.example.librarymanager.data.*;
import org.example.librarymanager.models.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AdminController extends ControllerWrapper {
    @FXML
    AnchorPane root;
    @FXML
    MFXButton exitBtn;
    @FXML
    Label userLabel;
    @FXML
    Label documentLabel;
    @FXML
    Label borrowtimeLabel;
    @FXML
    BarChart<String, Number> barChart;


    private int rootChildrenNum;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootChildrenNum = root.getChildren().size();
        showOverviewData();
//        userButtonOnClick();
        exitBtn.setOnAction(event -> {
            safeSwitchScene("home.fxml");
        });
    }

    private void setState(boolean state) {
        while(root.getChildren().size() > rootChildrenNum) {
            root.getChildren().remove(root.getChildren().size() - 1);
        }
        if (state) {
            for (Node node : root.getChildren()) {
                Common.enable(node);
            }
        } else {
            for (Node node : root.getChildren()) {
                Common.disable(node);
            }
        }
    }

    public void showOverviewData() {
        setState(true);
        userLabel.setText(String.valueOf(UserQuery.getInstance().count()));
        documentLabel.setText(String.valueOf(DocumentQuery.getInstance().count()));
        borrowtimeLabel.setText(String.valueOf(ServiceQuery.getInstance().count()));

        barChart.getData().clear();
        List<Service> serviceList = ServiceQuery.getInstance().getAll();
        XYChart.Series<String, Number> borrowData = new XYChart.Series<>();
        borrowData.setName("Borrow time");
        XYChart.Series<String, Number> returnData = new XYChart.Series<>();
        returnData.setName("Return time");

        Map<String, Integer> data1 = new HashMap<>();
        Map<String, Integer> data2 = new HashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 9; i >= 0; i--) {
            data1.put(String.valueOf(today.minusDays(i)), 0);
            data2.put(String.valueOf(today.minusDays(i)), 0);
        }

        for (Service service : serviceList) {
            Period period = Period.between(service.getBorrowDate(), today);
            if (period.getDays() <= 9) {
                String day = String.valueOf(service.getBorrowDate());
                data1.put(day, data1.get(day) + 1);
            }
            period = Period.between(service.getReturnDate(), today);
            if (period.getDays() <= 9) {
                String day = String.valueOf(service.getReturnDate());
                data2.put(day, data2.get(day) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : data1.entrySet()) {
            borrowData.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<String, Integer> entry : data2.entrySet()) {
            returnData.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

//        borrowData.getData().add(new XYChart.Data<>("30-11-2024", 50));
//        borrowData.getData().add(new XYChart.Data<>("31-11-2024", 65));
//        returnData.getData().add(new XYChart.Data<>("30-11-2024", 40));
//        returnData.getData().add(new XYChart.Data<>("31-11-2024", 80));

        barChart.getData().add(borrowData);
        barChart.getData().add(returnData);

    }

    public void userButtonOnClick() {
        setState(false);
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<User>> usersFu = executor.submit(() -> UserQuery.getInstance().getAll());
        try {
            List<User> users = usersFu.get();
            DataTable<User> dataTable = new DataTable<>("Users data", users, User.class, UserQuery.getInstance());
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    public void documentButtonOnClick() {
        setState(false);
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<Document>> documentsFu = executor.submit(() -> DocumentQuery.getInstance().getAll());
        try {
            List<Document> documents = documentsFu.get();
            DataTable<Document> dataTable = new DataTable<>("Documents data", documents, Document.class, DocumentQuery.getInstance());
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    public void categoryButtonOnClick() {
        setState(false);
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<Category>> categoriesFu = executor.submit(() -> CategoryQuery.getInstance().getAll());
        try {
            List<Category> categories = categoriesFu.get();
            DataTable<Category> dataTable = new DataTable<>("Categories data", categories, Category.class, CategoryQuery.getInstance());
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ratingButtonOnClick() {
        setState(false);
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<Rating>> ratingsFu = executor.submit(() -> RatingQuery.getInstance().getAll());
        try {
            List<Rating> ratings = ratingsFu.get();
            DataTable<Rating> dataTable = new DataTable<>("Ratings data", ratings, Rating.class, RatingQuery.getInstance());
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void serviceButtonOnClick() {
        setState(false);
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newFixedThreadPool(1);
        Future<List<Service>> servicesFu = executor.submit(() -> ServiceQuery.getInstance().getAll());
        try {
            List<Service> services = servicesFu.get();
            DataTable<Service> dataTable = new DataTable<>("Services data", services, Service.class, ServiceQuery.getInstance());
            root.getChildren().add(dataTable.getContainer());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
