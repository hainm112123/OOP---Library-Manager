package org.example.librarymanager.components;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.librarymanager.Common;
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.models.Document;

import java.awt.event.MouseEvent;

public class NotificationComponent implements Component {
    private HBox container;
    private VBox content;
    private VBox imageWrapper;
    private ImageView imageView;
    private Label title;
    private Label message;
    private ControllerWrapper controller;

    private static final int IMAGE_WIDTH = 32;
    private static final int IMAGE_HEIGHT = 48;

    public NotificationComponent(Document document, ControllerWrapper controller) {
        this.controller = controller;
        container = new HBox();
        content = new VBox();
        imageWrapper = new VBox();
        imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/example/librarymanager/image/no_image.jpg")));
        title = new Label("You should return this book soon!");
        message = new Label("You have borrowed \"" + document.getTitle() + "\" more than 14 days, you should return it soon. Otherwise, you must pay fine due to overdue.");

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

        imageWrapper.getChildren().add(imageView);
        imageWrapper.setPadding(new Insets(0, 3, 0, 0));

        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        message.setStyle("-fx-font-size: 10px");
        message.setPrefHeight(60);
        message.setMaxHeight(60);
        message.setWrapText(true);
        content.getChildren().addAll(title, message);
        content.setPadding(new Insets(0, 0, 0, 3));

        container.getChildren().add(imageWrapper);
        container.getChildren().add(content);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10, 8, 10, 8));
        container.setOnMouseEntered(e -> {
            container.setStyle("-fx-background-color: " + Common.TOPBAR_DROPDOWN_BUTTON_BG_HOVER + ";");
        });
        container.setOnMouseExited(e -> {
            container.setStyle("-fx-background-color: " + Common.TOPBAR_DROPDOWN_BUTTON_BG + ";");
        });
        container.setOnMouseClicked(e -> {
            ControllerWrapper.setCurrentDocument(document);
            controller.safeSwitchScene("document-detail.fxml");
        });
    }

    @Override
    public Node getElement() {
        return container;
    }
}
