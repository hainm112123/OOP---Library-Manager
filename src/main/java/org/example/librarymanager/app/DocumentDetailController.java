package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.DialogComponent;
import org.example.librarymanager.components.RatingComponent;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Rating;
import org.example.librarymanager.models.Service;
import org.example.librarymanager.models.User;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class DocumentDetailController extends ControllerWrapper {
    @FXML
    private AnchorPane container;
    @FXML
    private ImageView imageView;
    @FXML
    private Label title;
    @FXML
    private Label author;
    @FXML
    private Label category;
    @FXML
    private Label numberAvailable;
    @FXML
    private Label addDate;
    @FXML
    private Label owner;
    @FXML
    private Label borrowedTimes;
    @FXML
    private Label rating;
    @FXML
    private MFXButton borrowBtn;
    @FXML
    private MFXButton returnBtn;
    @FXML
    private MFXButton editBtn;
    @FXML
    private MFXButton addWishlistBtn;
    @FXML
    private MFXButton removeWishlistBtn;
    @FXML
    private TextArea description;

    @FXML
    private VBox ratingsBox;
    @FXML
    private Pagination pagination;
    @FXML
    private MFXScrollPane scrollScreen;
    @FXML
    private MFXProgressSpinner loader;
    @FXML
    private MFXProgressSpinner wishlistLoader;

    @FXML
    private HBox backBtn;
    @FXML
    private Label header;

    /**
     * Set rating box.
     */
    public void setRatingsBox(List<Rating> ratings, int start, int end) {
        ratingsBox.getChildren().clear();
        for (int i = start; i < Math.min(end, ratings.size()); ++ i) {
            Rating rating = ratings.get(i);
            ratingsBox.getChildren().add(new RatingComponent(rating.getValue(), rating.getContent(), rating.getPostedTime(), rating.getUserId()).getElement());
        }
    }

    /**
     * Initialization.
     * Show details from current document.
     * Executor manages 5 threads: status, category, image, ratings, owner.
     * TaskExe manages 1 single threads: borrow or return.
     * Ratings are displayed in pagination, each page contains 5 ratings.
     * Edit button switch scene to edit-document.
     * Borrow button, return button updates data in sql then switch scene to document-detail.
     * @param location url to document-detail.fxml
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(getCurrentDocument().getTitle());
        author.setText(getCurrentDocument().getAuthor());
        numberAvailable.setText(""+getCurrentDocument().getQuantityInStock());
        addDate.setText(""+getCurrentDocument().getAddDate());
        borrowedTimes.setText(""+getCurrentDocument().getBorrowedTimes());
        description.setText(getCurrentDocument().getDescription());
        Common.disable(loader);
        Common.disable(wishlistLoader);
        header.setText(title.getText());
        TranslateTransition left = new TranslateTransition(Duration.millis(200), backBtn);
        left.setToX(-6);
        TranslateTransition right = new TranslateTransition(Duration.millis(200), backBtn);
        right.setToX(0);
        backBtn.setOnMouseEntered(e -> left.playFromStart());
        backBtn.setOnMouseExited(e -> right.playFromStart());
        backBtn.setOnMouseClicked(e -> backScene());

        executor = Executors.newFixedThreadPool(5);
        Future<Integer> statusFu = executor.submit(() -> ServiceQuery.getInstance().getStatus(getUser().getId(), getCurrentDocument().getId()));
        Future<Category> categoryFu = executor.submit(() -> CategoryQuery.getInstance().getById(getCurrentDocument().getCategoryId()));
        Future<Image> imageFu = executor.submit(() -> {
            try {
                return new Image(getCurrentDocument().getImageLink(), true);
            } catch (Exception e) {
                e.printStackTrace();
                return new Image(getClass().getResourceAsStream("/org/example/librarymanager/image/no_image.jpg"));
            }
        });
        Future<List<Rating>> ratingsFu = executor.submit(() -> DocumentQuery.getInstance().getDocumentRatings(getCurrentDocument().getId()));
        Future<User> ownerFu = executor.submit(() -> UserQuery.getInstance().getById(getCurrentDocument().getOwner()));
        executor.shutdown();

        try {
            int status = statusFu.get();
            if (status == Service.STATUS_WISH_LIST) {
                Common.disable(addWishlistBtn);
                Common.enable(removeWishlistBtn);
                Common.enable(borrowBtn);
                Common.disable(returnBtn);
            } else if (status == Service.STATUS_READING) {
                Common.disable(borrowBtn);
                Common.enable(returnBtn);
                Common.disable(addWishlistBtn);
                Common.disable(removeWishlistBtn);
            }
            else {
                Common.enable(borrowBtn);
                Common.disable(returnBtn);
                Common.enable(addWishlistBtn);
                Common.disable(removeWishlistBtn);
            }
            if (ownerFu.get().getId() == getUser().getId()) {
                Common.disable(borrowBtn);
                Common.disable(returnBtn);
                Common.disable(addWishlistBtn);
                Common.disable(removeWishlistBtn);
                Common.enable(editBtn);
            } else {
                Common.disable(editBtn);
            }
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
                safeSwitchScene("edit-document.fxml");
            });
            borrowBtn.setOnMouseClicked(e -> {
                Common.disable(borrowBtn);
                Common.enable(loader);
                Task<Boolean> task = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return ServiceQuery.getInstance().borrowDocument(getUser().getId(), getCurrentDocument());
                    }
                };
                task.setOnSucceeded(event -> {
                    Common.disable(loader);
                    Common.enable(borrowBtn);
                    if (task.getValue()) {
                        safeSwitchScene("document-detail.fxml");
                    } else {
                        DialogComponent dialog = new DialogComponent(
                                "",
                                "There is no more book currently available. Please comback later.",
                                DialogComponent.DIALOG_INFO,
                                stage,
                                container
                        );
                        dialog.addConfirmAction(e1 -> {
                            dialog.close();
                        });
                        dialog.show();
                    }
                });
                new Thread(task).start();
            });
            returnBtn.setOnAction(e -> {
                Common.disable(returnBtn);
                Common.enable(loader);
                Task<Boolean> task = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return ServiceQuery.getInstance().returnDocument(getUser().getId(), getCurrentDocument());
                    }
                };
                task.setOnSucceeded(event -> {
                    Common.disable(loader);
                    Common.enable(returnBtn);
                    if (task.getValue()) {
                        safeSwitchScene("rating.fxml");
                    } else {
                        DialogComponent dialog = new DialogComponent(
                                "",
                                "Some error occurs. Please try again.",
                                DialogComponent.DIALOG_ERROR,
                                stage,
                                container
                        );
                        dialog.addConfirmAction(e1 -> {
                            dialog.close();
                        });
                        dialog.show();
                    }
                });
                new Thread(task).start();
            });
            addWishlistBtn.setOnMouseClicked(e -> {
                Common.disable(addWishlistBtn);
                Common.enable(wishlistLoader);
                Task<Boolean> task = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return ServiceQuery.getInstance().addToWishlist(getUser().getId(), getCurrentDocument().getId());
                    }
                };
                task.setOnSucceeded(event -> {
                    Common.disable(wishlistLoader);
                    Common.enable(removeWishlistBtn);
                    if (task.getValue()) {
                        safeSwitchScene("document-detail.fxml");
                    } else {
                        DialogComponent dialog = new DialogComponent(
                                "",
                                "Some error occurs. Please try again.",
                                DialogComponent.DIALOG_WARNING,
                                stage,
                                container
                        );
                        dialog.addConfirmAction(e1 -> {
                            dialog.close();
                        });
                        dialog.show();
                    }
                });
                new Thread(task).start();
            });
            removeWishlistBtn.setOnMouseClicked(e -> {
                Common.disable(removeWishlistBtn);
                Common.enable(wishlistLoader);
                Task<Boolean> task = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return ServiceQuery.getInstance().removeFromWishlist(getUser().getId(), getCurrentDocument().getId());
                    }
                };
                task.setOnSucceeded(event -> {
                    Common.disable(wishlistLoader);
                    Common.enable(addWishlistBtn);
                    if (task.getValue()) {
                        safeSwitchScene("document-detail.fxml");
                    } else {
                        DialogComponent dialog = new DialogComponent(
                                "",
                                "Some error occurs. Please try again.",
                                DialogComponent.DIALOG_WARNING,
                                stage,
                                container
                        );
                        dialog.addConfirmAction(e1 -> {
                            dialog.close();
                        });
                        dialog.show();
                    }
                });
                new Thread(task).start();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}