package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.NotificationComponent;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.data.Trie;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.User;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TopbarController extends ControllerWrapper {
    @FXML
    Button topbarHomeBtn;
    @FXML
    private ComboBox<String> topbarCategoryBtn;
    @FXML
    ImageView userBtn;
    @FXML
    TextField searchBox;
    @FXML
    VBox suggestionsBox;
    @FXML
    ScrollPane suggestionsScrollPane;
    @FXML
    StackPane pane;
    @FXML
    AnchorPane notificationBell;
    @FXML
    Label notificationBadge;
    @FXML
    VBox notificationPane;
    @FXML
    VBox notificationBox;
    @FXML
    MFXScrollPane userPane;
    @FXML
    Label usernameLabel;
    @FXML
    Label usertypeLabel;
    @FXML
    VBox userBox;
    @FXML
    HBox profileBtn;
    @FXML
    HBox changePasswordBtn;
    @FXML
    HBox bookshelfBtn;
    @FXML
    HBox mydocBtn;
    @FXML
    HBox newdocBtn;
    @FXML
    HBox manageBtn;
    @FXML
    HBox signoutBtn;

    private Future<List<String>> categoryFu;
    private Future<List<Document>> documentFu;

    private void initCategory() {
        try {
            List<String> categoryList = categoryFu.get();
            ObservableList<String> list = FXCollections.observableArrayList(categoryList);
            topbarCategoryBtn.setItems(list);
            topbarCategoryBtn.setOnAction((event) -> {
                ControllerWrapper.setCurrentCategory(topbarCategoryBtn.getValue());
                safeSwitchScene("category.fxml");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRedirect() {
        topbarHomeBtn.setOnAction((event) -> safeSwitchScene("home.fxml"));
    }

    private void initSearchBox() {
        if (Trie.getInstance().getCnt() == 0) {
            Trie.getInstance().buildTrie();
        }
        suggestionsScrollPane.setVisible(false);
        searchBox.setOnKeyReleased(event -> {
            Pair<Trie.Node, Trie.Node> range = Trie.getInstance().getRange(searchBox.getText());
            displaySuggestionPane(range.getKey(), range.getValue());
            suggestionsScrollPane.setVisible(true);
        });
    }

    private void initNotification() {
        Common.disable(notificationPane);
        try {
            List<Document> documents = documentFu.get();
            notificationBadge.setText(""+documents.size());
            if (documents.isEmpty()) {
                Common.disable(notificationBadge);
            }
            else {
                Common.enable(notificationBadge);
            }
            for (Document document : documents) {
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
            }
            notificationBell.setOnMouseClicked(event -> {
               if (notificationPane.isDisable()) {
                   Common.enable(notificationPane);
               } else {
                   Common.disable(notificationPane);
               }
            });
            DropShadow ds = new DropShadow();
            ds.setRadius(10);
            notificationPane.setEffect(ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUser() {
        usernameLabel.setText(getUser().getUsername());
        usertypeLabel.setText(User.USER_TYPE_STRING[getUser().getPermission()]);
        profileBtn.setOnMouseClicked(event -> safeSwitchScene("profile.fxml"));
        changePasswordBtn.setOnMouseClicked(event -> {});
        bookshelfBtn.setOnMouseClicked(event -> safeSwitchScene("borrowing-documents.fxml"));
        mydocBtn.setOnMouseClicked(event -> safeSwitchScene("my-documents.fxml"));
        newdocBtn.setOnMouseClicked(event -> safeSwitchScene("new-document.fxml"));
        manageBtn.setOnMouseClicked(event -> safeSwitchScene("admin.fxml"));
        signoutBtn.setOnMouseClicked(event -> {
            safeSwitchScene("login.fxml");
            setUser(null);
        });
        for (Node node: userBox.getChildren()) {
            if (node instanceof HBox) {
                node.setOnMouseEntered(e -> {
                    node.setStyle("-fx-background-color: " + Common.TOPBAR_DROPDOWN_BUTTON_BG_HOVER + ";");
                });
                node.setOnMouseExited(e -> {
                    node.setStyle("-fx-background-color: " + Common.TOPBAR_DROPDOWN_BUTTON_BG + ";");
                });
            }
        }
        if (getUser().getPermission() == User.TYPE_USER) {
            userBox.getChildren().subList(7, 10).clear();
            userBox.setPrefHeight(400);
            userBox.setMaxHeight(400);
        } else if (getUser().getPermission() == User.TYPE_MODERATOR) {
            userBox.getChildren().subList(9, 10).clear();
            userBox.setPrefHeight(480);
            userBox.setMaxHeight(480);
        }
        userBtn.setOnMouseClicked(e -> {
            if (userPane.isDisable()) {
                Common.enable(userPane);
            } else {
                Common.disable(userPane);
            }
        });
        Common.disable(userPane);
    }

    /**
     * Initialization.
     * Create layout with buttons to switch to main scenes.
     * @param location url to top bar.fxml
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newFixedThreadPool(2);
        categoryFu = executor.submit(() -> CategoryQuery.getInstance().getCategoriesName());
        documentFu = executor.submit(() -> ServiceQuery.getInstance().getOverdueDocuments(getUser().getId()));
        executor.shutdown();

        try {
            initRedirect();
            initUser();
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            executorService.submit(this::initCategory);
            executorService.submit(this::initSearchBox);
            executorService.submit(this::initNotification);
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            stage.getScene().setOnMousePressed(event -> {
                if (!notificationBell.getBoundsInParent().contains(event.getX(), event.getY())
                        && !notificationPane.getBoundsInParent().contains(event.getX(), event.getY())) {
                    Common.disable(notificationPane);
                }

                if (!userBtn.getBoundsInParent().contains(event.getX(), event.getY())
                && !userPane.getBoundsInParent().contains(event.getX(), event.getY())) {
                    Common.disable(userPane);
                }

                if (!searchBox.getBoundsInParent().contains(event.getX(), event.getY())) {
                    suggestionsScrollPane.setVisible(false);
                    pane.requestFocus();
                }
            });
        });
    }

    private void displaySuggestionPane(Trie.Node first, Trie.Node last) {
        suggestionsBox.getChildren().clear();
        int buttonH = 30;
        if(first == null || last == null) {
            Button button = new Button();
            button.setText("No title found");
            button.setPrefHeight(buttonH);
            button.setMinHeight(buttonH);
            button.setMaxHeight(buttonH);
            button.setStyle("-fx-background-color: #FFFFFF;-fx-padding: 0 0 0 0;");
            suggestionsBox.getChildren().add(button);
            suggestionsBox.setPrefHeight(buttonH);
            suggestionsScrollPane.setPrefHeight(buttonH);
            return;
        }
        int size = 0;
        for(int i = 1; i <= 20; i++) {
            Button button = new Button();
            button.setText(first.getString());
            button.setPrefHeight(buttonH);
            button.setMinHeight(buttonH);
            button.setMaxHeight(buttonH);
            button.setUserData((Integer)first.getId());
            button.setStyle("-fx-background-color: #FFFFFF;-fx-padding: 0 0 0 0;");
            button.setOnAction(event -> {
                Button clickedButton = (Button) event.getSource();
                int id = (Integer)clickedButton.getUserData();
                setCurrentDocument(DocumentQuery.getInstance().getById(id));
                safeSwitchScene("document-detail.fxml");
                //System.out.println("On clicked");
            } );
            suggestionsBox.getChildren().add(button);
            size ++;
            if (first == last) break;
            first = first.getNex();
        }
//        buttonH += 10;
        suggestionsBox.setPrefHeight(size * buttonH);
        suggestionsScrollPane.setPrefHeight(Math.min(size * buttonH, 300));
    }
}