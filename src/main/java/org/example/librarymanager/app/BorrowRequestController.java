package org.example.librarymanager.app;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.librarymanager.components.BorrowRequestComponent;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.models.PendingService;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.concurrent.Future;

public class BorrowRequestController extends ControllerWrapper {
    @FXML
    private VBox container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newSingleThreadExecutor();
        Future<List<PendingService>> future = executor.submit(() -> ServiceQuery.getInstance().getPendingServices());
        try {
            List<PendingService> services = future.get();
            for (PendingService service : services) {
                container.getChildren().add(new BorrowRequestComponent(service).getElement());
            }
            container.setAlignment(Pos.TOP_CENTER);
            container.setPrefHeight(services.size() * BorrowRequestComponent.COMPONENT_HEIGHT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
