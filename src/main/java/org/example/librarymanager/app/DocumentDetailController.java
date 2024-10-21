package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.RatingComponent;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Rating;
import org.example.librarymanager.models.User;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class DocumentDetailController extends ControllerWrapper {
    @FXML
    ImageView imageView;
    @FXML
    Label title;
    @FXML
    Label author;
    @FXML
    Label category;
    @FXML
    Label numberAvailable;
    @FXML
    Label addDate;
    @FXML
    Label owner;
    @FXML
    Label borrowedTimes;
    @FXML
    Label rating;
    @FXML
    MFXButton borrowBtn;
    @FXML
    MFXButton returnBtn;
    @FXML
    MFXButton editBtn;
    @FXML
    TextArea description;

    @FXML
    VBox ratingsBox;
    @FXML
    Pagination pagination;
    @FXML
    MFXScrollPane scrollScreen;
    @FXML
    MFXProgressSpinner loader;

    public void setRatingsBox(List<Rating> ratings, int start, int end) {
        ratingsBox.getChildren().clear();
        for (int i = start; i < Math.min(end, ratings.size()); ++ i) {
            Rating rating = ratings.get(i);
            ratingsBox.getChildren().add(new RatingComponent(rating.getValue(), rating.getContent(), rating.getUserId()).getContainer());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(getCurrentDocument().getTitle());
        author.setText(getCurrentDocument().getAuthor());
        numberAvailable.setText(""+getCurrentDocument().getQuantityInStock());
        addDate.setText(""+getCurrentDocument().getAddDate());
        borrowedTimes.setText(""+getCurrentDocument().getBorrowedTimes());
        description.setText(getCurrentDocument().getDescription());
        loader.setVisible(false);

        ExecutorService executor = Executors.newFixedThreadPool(5);
        Future<Boolean> borrowStatusFu = executor.submit(() -> ServiceQuery.isBorrowingDocument(getUser().getId(), getCurrentDocument().getId()));
        Future<Category> categoryFu = executor.submit(() -> CategoryQuery.getCategory(getCurrentDocument().getCategoryId()));
        Future<Image> imageFu = executor.submit(() -> {
            try {
                return new Image(getCurrentDocument().getImageLink(), true);
            } catch (Exception e) {
                e.printStackTrace();
                return new Image(getClass().getResourceAsStream("/org/example/librarymanager/image/no_image.jpg"));
            }
        });
        Future<List<Rating>> ratingsFu = executor.submit(() -> DocumentQuery.getDocumentRatings(getCurrentDocument().getId()));
        Future<User> ownerFu = executor.submit(() -> UserQuery.getUserById(getCurrentDocument().getOwner()));
        executor.shutdown();

        try {
            if (borrowStatusFu.get()) {
                Common.disable(borrowBtn);
                Common.enable(returnBtn);
            }
            else {
                Common.enable(borrowBtn);
                Common.disable(returnBtn);
            }
            if (ownerFu.get().getId() == getUser().getId()) {
                Common.disable(borrowBtn);
                Common.disable(returnBtn);
            }
            editBtn.setVisible(ownerFu.get().getId() == getUser().getId());
            owner.setText(ownerFu.get().getFirstname() + " " + ownerFu.get().getLastname());
            category.setText(categoryFu.get().getName());
            imageView.setImage(imageFu.get());

            List<Rating> ratings = ratingsFu.get();
            if (!ratings.isEmpty()) {
                rating.setText(String.format("%.1f/5", ratings.stream().mapToDouble(Rating::getValue).average().orElse(0.0)));
            }
            else {
                rating.setText("No review");
            }

            setRatingsBox(ratings, 0, 5);
            pagination.setPageCount((ratings.size() - 1) / 5 + 1);
            pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
                setRatingsBox(ratings, newValue.intValue() * 5, newValue.intValue() * 5 + 5);
                Platform.runLater(() -> {
                    scrollScreen.applyCss();
                    scrollScreen.layout();
                    scrollScreen.setVvalue(1);
                });
            });

            editBtn.setOnMouseClicked(e -> {
                switchScene("edit-document.fxml");
            });
            borrowBtn.setOnMouseClicked(e -> {
                Common.disable(borrowBtn);
                Common.enable(loader);
                ExecutorService taskExe = Executors.newSingleThreadExecutor();
                Future<Boolean> future = taskExe.submit(() -> ServiceQuery.borrowDocument(getUser().getId(), getCurrentDocument().getId()));
                taskExe.shutdown();
                try {
                    if (future.get()) {
                        Common.disable(loader);
                        switchScene("document-detail.fxml");
                    }
                } catch (InterruptedException | ExecutionException exception) {
                    exception.printStackTrace();
                }
            });
            returnBtn.setOnAction(e -> {
                Common.disable(borrowBtn);
                Common.enable(loader);
                ExecutorService taskExe = Executors.newSingleThreadExecutor();
                Future<Boolean> future = taskExe.submit(() -> ServiceQuery.returnDocument(getUser().getId(), getCurrentDocument().getId()));
                taskExe.shutdown();
                try {
                    if (future.get()) {
                        Common.disable(loader);
                        switchScene("document-detail.fxml");
                    }
                } catch (InterruptedException | ExecutionException exception) {
                    exception.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
