package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        editBtn.setOnMouseClicked(e -> ControllerWrapper.switchScene("edit-document.fxml"));

        ExecutorService executor = Executors.newFixedThreadPool(5);
        Future<Boolean> borrowStatusFu = executor.submit(() -> ServiceQuery.isBorrowingDocument(getUser().getId(), getCurrentDocument().getId()));
        Future<Category> categoryFu = executor.submit(() -> CategoryQuery.getCategory(getCurrentDocument().getCategoryId()));
        Future<Image> imageFu = executor.submit(() -> new Image(getCurrentDocument().getImageLink(), true));
        Future<List<Rating>> ratingsFu = executor.submit(() -> DocumentQuery.getDocumentRatings(getCurrentDocument().getId()));
        Future<User> ownerFu = executor.submit(() -> UserQuery.getUserById(getCurrentDocument().getOwner()));

        try {
            if (borrowStatusFu.get()) {
                borrowBtn.setVisible(false);
                returnBtn.setVisible(true);
            }
            else {
                borrowBtn.setVisible(true);
                returnBtn.setVisible(false);
            }
            if (ownerFu.get().getId() == getUser().getId()) {
                borrowBtn.setVisible(false);
                returnBtn.setVisible(false);
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
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
