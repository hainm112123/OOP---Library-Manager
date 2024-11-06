package org.example.librarymanager.components;

import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.models.Document;

public class DocumentComponent {
    private AnchorPane container;
    private VBox box;
    private ImageView imageView;
    private Label title;
    private ControllerWrapper controller;

    public static final int DOC_COMPONENT_WITDH = 144;
    public static final int DOC_COMPONENT_HEIGHT = 300;
    public static final int DOC_COMPONENT_OFFSET = 30;

    private static final int IMAGE_WIDTH = 144;
    private static final int IMAGE_HEIGHT = 216;
    private static final DropShadow ds = new DropShadow();
    private static final DropShadow ds_hover = new DropShadow();

    static {
        ds.setRadius(15);
        ds_hover.setRadius(30);
    }

    /**
     * Construct a component of document details in a VBox.
     * Image is loaded in a task which is executed in a new distinct thread.
     * @param document document to display
     * @param controller current controller
     */
    public DocumentComponent(Document document, ControllerWrapper controller) {
        box = new VBox();
        container = new AnchorPane();

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
        box.getChildren().add(imageView);

        title = new Label(document.getTitle());
        title.getStyleClass().add("title");
        title.setMinWidth(DOC_COMPONENT_WITDH);
        title.setPrefWidth(DOC_COMPONENT_WITDH);
        title.setMaxWidth(DOC_COMPONENT_WITDH);
        title.setEllipsisString("...");
        title.alignmentProperty().set(Pos.CENTER);
        box.getChildren().add(title);
        box.setAlignment(Pos.CENTER);
        container.getChildren().add(box);
        container.getStylesheets().add(getClass().getResource("/org/example/librarymanager/css/document.css").toExternalForm());

        container.setOnMouseClicked((event) -> {
            ControllerWrapper.setCurrentDocument(document);
            controller.safeSwitchScene("document-detail.fxml");
        });

        TranslateTransition up = new TranslateTransition(Duration.millis(200), imageView);
        up.setToY(-10);
        TranslateTransition down = new TranslateTransition(Duration.millis(200), imageView);
        down.setToY(0);
        imageView.setEffect(ds);
        container.setOnMouseEntered(e -> {
            imageView.setEffect(ds_hover);
            up.playFromStart();
        });
        container.setOnMouseExited(e -> {
            imageView.setEffect(ds);
            down.playFromStart();
        });
    }

    /**
     * Return container.
     */
    public Node getElement() {
        return container;
    }
}
