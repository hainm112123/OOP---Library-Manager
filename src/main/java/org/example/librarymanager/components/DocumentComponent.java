package org.example.librarymanager.components;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.models.Document;

public class DocumentComponent {
    private VBox container;
    private ImageView imageView;
    private Label title;

    private static final int IMAGE_WIDTH = 128;
    private static final int IMAGE_HEIGHT = 192;
    public static final int DOC_COMPONENT_WITDH = 200;
    public static final int DOC_COMPONENT_OFFSET = 30;

    public DocumentComponent(Document document) {
        container = new VBox();

        imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/example/librarymanager/image/no_image.jpg")));
        if (document.getImageLink() != null) {
            Task<Image> task = new Task<Image>() {
                @Override
                protected Image call() {
                    try {
                        return new Image(document.getImageLink(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new Image(getClass().getResourceAsStream("/org/example/librarymanager/image/no_image.jpg"));
                    }
                }
            };
            task.setOnSucceeded((event) -> imageView.setImage(task.getValue()));
            new Thread(task).start();
        }
        imageView.setFitWidth(IMAGE_WIDTH);
        imageView.setFitHeight(IMAGE_HEIGHT);
        container.getChildren().add(imageView);

        title = new Label(document.getTitle());
        title.getStyleClass().add("title");
        title.setMinWidth(DOC_COMPONENT_WITDH);
        title.setPrefWidth(DOC_COMPONENT_WITDH);
        title.setMaxWidth(DOC_COMPONENT_WITDH);
        title.setEllipsisString("...");
        title.alignmentProperty().set(Pos.CENTER);
        container.getChildren().add(title);

        container.setAlignment(Pos.CENTER);

        container.setOnMouseClicked((event) -> {
            ControllerWrapper.setCurrentDocument(document);
            ControllerWrapper.switchScene("document-detail.fxml");
        });
    }

    public VBox getElement() {
        return container;
    }
}
