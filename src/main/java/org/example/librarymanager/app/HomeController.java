package org.example.librarymanager.app;

import javafx.concurrent.Task;
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
    private static final int IMAGE_WIDTH = 128;
    private static final int IMAGE_HEIGHT = 192;

    /**
     * get a document image, name,... to display
     * @return
     */
    private VBox documentComponent(Document document) {
        VBox container = new VBox();

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/example/librarymanager/image/no_image.jpg")));
        if (document.getImageLink() != null) {
            Task<Image> task = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(document.getImageLink(), true);
                }
            };
            task.setOnSucceeded((event) -> imageView.setImage(task.getValue()));
            new Thread(task).start();
        }
        imageView.setFitWidth(IMAGE_WIDTH);
        imageView.setFitHeight(IMAGE_HEIGHT);
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
