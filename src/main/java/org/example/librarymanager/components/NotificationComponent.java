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
import org.example.librarymanager.data.NotificationQuery;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Notification;

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
    public static final int COMPONENT_HEIGHT = 68;

    /**
     * notification.
     * @param document
     * @param notification
     * @param controller
     * @param titleStr
     * @param messageStr
     * @param destination
     * @param pane
     */
    public NotificationComponent(Document document, Notification notification, ControllerWrapper controller, String titleStr, String messageStr, String destination, Node pane) {
        this.controller = controller;
        container = new HBox();
        content = new VBox();
        imageWrapper = new VBox();
        imageView = document != null ? new ImageView(Common.NO_IMAGE) : new ImageView(new Image(getClass().getResourceAsStream("/org/example/librarymanager/image/logo_notext.png")));
        title = new Label(titleStr);
        message = new Label(messageStr);

        if (document != null && document.getImageLink() != null) {
            Task<Image> task = new Task<Image>() {
                @Override
                protected Image call() {
                    try {
                        return new Image(document.getImageLink(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Common.NO_IMAGE;
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
            if (document != null) {
                ControllerWrapper.setCurrentDocument(document);
            }
            if (notification != null) {
                Task<Boolean> task = new Task<>() {
                    @Override
                    protected Boolean call() {
                        notification.setStatus(Notification.STATUS.READ.ordinal());
                        return NotificationQuery.getInstance().update(notification);
                    }
                };
                new Thread(task).start();
            }
            Common.disable(pane);
            controller.safeSwitchScene(destination);
        });
    }

    @Override
    public Node getElement() {
        return container;
    }
}
