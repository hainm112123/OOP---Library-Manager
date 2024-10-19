package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.User;

import java.net.URL;
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
        Future<Double> ratingFu = executor.submit(() -> DocumentQuery.getDocumentRating(getCurrentDocument().getId()));
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
            editBtn.setVisible(ownerFu.get().getId() == getUser().getId());
            owner.setText(ownerFu.get().getFirstname() + " " + ownerFu.get().getLastname());
            category.setText(categoryFu.get().getName());
            imageView.setImage(imageFu.get());
            if (ratingFu.get() != null) {
                rating.setText(ratingFu.get()+"/5");
            } else {
                rating.setText("No review");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
