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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
        List<Document> mostPopularDocuments = DocumentQuery.getMostPopularDocuments(15);
        List<Document> highestRatedDocuments = DocumentQuery.getHighestRatedDocuments(15);
        display(mostPopularDocuments, mostPopularContainer);
        display(highestRatedDocuments, highestRatedContainer);
    }
}
