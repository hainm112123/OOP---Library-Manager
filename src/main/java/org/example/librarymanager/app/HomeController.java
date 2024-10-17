package org.example.librarymanager.app;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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

    private static final int DOC_COMPONENT_WITDH = 200;
    private static final int DOC_COMPONENT_OFFSET = 30;

    /**
     * get a document image, name,... to display
     * @return
     */
    private VBox documentComponent(Document document) {
        VBox container = new VBox();

        String imageUrl = document.getImageLink() != null ? document.getImageLink() : "/image/Icon.png";
        Image image = new Image(imageUrl);
        ImageView imageView = new ImageView(image);
        container.getChildren().add(imageView);

        Label title = new Label(document.getTitle());
        title.getStyleClass().add("title");
        title.setMaxWidth(DOC_COMPONENT_WITDH);
        title.setEllipsisString("...");
        title.alignmentProperty().set(Pos.CENTER);
        container.getChildren().add(title);

        container.setAlignment(Pos.CENTER);

        return container;
    }

    private void display(List<Document> documents, AnchorPane container) {
        for (int i = 0; i < documents.size(); ++ i) {
            Document document = documents.get(i);
            VBox doc = documentComponent(document);
            AnchorPane.setLeftAnchor(doc, (double)(i * (DOC_COMPONENT_WITDH + DOC_COMPONENT_OFFSET)));
            container.getChildren().add(doc);
        }
        container.setPrefWidth(documents.size() * (DOC_COMPONENT_WITDH + DOC_COMPONENT_OFFSET));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Document> mostPopularDocuments = DocumentQuery.getMostPopularDocuments(15);
        List<Document> highestRatedDocuments = DocumentQuery.getHighestRatedDocuments(15);
        display(mostPopularDocuments, mostPopularContainer);
        display(highestRatedDocuments, highestRatedContainer);
    }
}
