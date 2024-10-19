package org.example.librarymanager.components;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import lombok.Data;
import org.controlsfx.control.Rating;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.User;

@Data
public class RatingComponent {
    private VBox container;
    private Rating rating;
    private TextArea content;
    private Label user;

    public RatingComponent(double value, String ratingContent, int userId) {
        user = new Label();
        container = new VBox();
        rating = new Rating();
        content = new TextArea();

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                User user = UserQuery.getUserById(userId);
                return user.getFirstname() + " " + user.getLastname();
            }
        };
        task.setOnSucceeded(e -> user.setText((String) task.getValue()));
        new Thread(task).start();

        user.setPadding(new Insets(0, 0, 6, 0));

        rating.setRating(value);
        rating.setDisable(true);
        rating.getTransforms().add(new Scale(0.5, 0.5));

        AnchorPane contentContainer = new AnchorPane();
        content.setWrapText(true);
        content.setPrefWidth(1000);
        content.setPrefHeight(80);
        content.setText(ratingContent);
        content.setEditable(false);
        contentContainer.getChildren().add(content);

        container.getChildren().addAll(user, rating, contentContainer);
        container.setPadding(new Insets(0, 0, 32, 0));
    }
}
