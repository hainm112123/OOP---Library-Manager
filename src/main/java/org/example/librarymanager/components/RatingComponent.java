package org.example.librarymanager.components;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import lombok.Data;
import org.controlsfx.control.Rating;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class RatingComponent {
    private VBox container;
    private Rating rating;
    private TextArea content;
    private Label user;
    private Label time;

    /**
     * Construct a component of rating in a VBox.
     * Owner user details are loaded from sql in a distinct thread.
     * @param value rating value 0 - 5
     * @param ratingContent comment content of rating
     * @param postedTime time posted
     * @param userId id of rating owner
     */
    public RatingComponent(double value, String ratingContent, LocalDateTime postedTime, int userId) {
        user = new Label();
        container = new VBox();
        rating = new Rating();
        content = new TextArea();
        time = new Label();

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                User user = UserQuery.getInstance().getById(userId);
                return user.getFirstname() + " " + user.getLastname();
            }
        };
        task.setOnSucceeded(e -> user.setText((String) task.getValue()));
        new Thread(task).start();

        HBox userContainer = new HBox();
        time.setText(postedTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        time.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa");
        user.setPadding(new Insets(0, 4, 0, 0));
        userContainer.getChildren().addAll(user, time);
        userContainer.setAlignment(Pos.CENTER_LEFT);
        userContainer.setPadding(new Insets(0, 0, 6, 0));

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

        container.getChildren().addAll(userContainer, rating, contentContainer);
        container.setPadding(new Insets(0, 0, 32, 0));
    }
}