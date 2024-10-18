package org.example.librarymanager.app;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.example.librarymanager.components.DocumentComponent;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MyDocumentsController extends ControllerWrapper {
    private static final int NUM_COL = 4;
    private static final int ROW_HEIGHT = 250;

    @FXML
    GridPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Document> documents = DocumentQuery.getDocumentsByOwner(getUser().getId());
        int row = -1, col = 0;
        container.getRowConstraints().clear();
        for (Document document : documents) {
            if (col == 0) {
                row ++;
                container.setPrefHeight((double)(row + 1) * ROW_HEIGHT);
                RowConstraints rowConstraints = new RowConstraints();
                container.getRowConstraints().add(rowConstraints);
            }
            VBox box = new VBox();
            box.setPrefHeight(ROW_HEIGHT);
            box.getChildren().add(new DocumentComponent(document).getElement());
            box.setAlignment(Pos.CENTER);
            container.add(box, col, row);
            col = (col + 1) % NUM_COL;
        }
    }
}
