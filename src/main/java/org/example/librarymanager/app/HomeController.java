package org.example.librarymanager.app;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.example.librarymanager.components.DocumentComponent;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Document;

import javax.print.Doc;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class HomeController extends ControllerWrapper{
    @FXML
    AnchorPane mostPopularContainer;
    @FXML
    AnchorPane highestRatedContainer;

    private void display(List<Document> documents, AnchorPane container) {
        for (int i = 0; i < documents.size(); ++ i) {
            Document document = documents.get(i);
            VBox doc = new DocumentComponent(document).getElement();
            AnchorPane.setLeftAnchor(doc, (double)(i * (DocumentComponent.DOC_COMPONENT_WITDH + DocumentComponent.DOC_COMPONENT_OFFSET)));
            container.getChildren().add(doc);
        }
        container.setPrefWidth(documents.size() * (DocumentComponent.DOC_COMPONENT_WITDH + DocumentComponent.DOC_COMPONENT_OFFSET));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<List<Document>> mostPoularDocFu = executor.submit(() -> DocumentQuery.getMostPopularDocuments(15));
        Future<List<Document>> highestRateDocFu = executor.submit(() -> DocumentQuery.getHighestRatedDocuments(15));
        try {
            List<Document> mostPopularDocuments = mostPoularDocFu.get();
            List<Document> highestRatedDocuments = highestRateDocFu.get();
            executor.submit(() -> display(mostPopularDocuments, mostPopularContainer));
            executor.submit(() -> display(highestRatedDocuments, highestRatedContainer));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}
