package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Pair;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.NotificationComponent;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.data.Trie;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.User;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TopbarController extends ControllerWrapper {
    private static final int CATEGORY_CHOICE_WIDTH = 150;
    private static final int CATEGORY_CHOICE_HEIGHT = 42;

    @FXML
    private Button homeBtn;
    @FXML
    private Button categoryBtn;
    @FXML
    private Button advancedSearchBtn;
    @FXML
    private AnchorPane categoryPane;
    @FXML
    private GridPane categoryGrid;
    @FXML
    private ImageView userBtn;
    @FXML
    private TextField searchBox;
    @FXML
    private VBox suggestionsBox;
    @FXML
    private ScrollPane suggestionsScrollPane;
    @FXML
    private StackPane pane;
    @FXML
    private AnchorPane notificationBell;
    @FXML
    private Label notificationBadge;
    @FXML
    private VBox notificationPane;
    @FXML
    private VBox notificationBox;
    @FXML
    private MFXScrollPane userPane;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label usertypeLabel;
    @FXML
    private VBox userBox;
    @FXML
    private HBox profileBtn;
    @FXML
    private HBox changePasswordBtn;
    @FXML
    private HBox bookshelfBtn;
    @FXML
    private HBox mydocBtn;
    @FXML
    private HBox newdocBtn;
    @FXML
    private HBox manageBtn;
    @FXML
    private HBox signoutBtn;

    private Future<List<Category>> categoryFu;
    private Future<List<Document>> documentFu;

    private void initCategory() {
        try {
            List<Category> categoryList = categoryFu.get();
            int cols = 5;
            int rows = Math.max((categoryList.size() - 1) / cols + 1, 5);
            categoryGrid.setPrefHeight(rows * CATEGORY_CHOICE_HEIGHT);
            categoryPane.setPrefHeight(rows * CATEGORY_CHOICE_HEIGHT);
            categoryGrid.getChildren().clear();
            for (int i = 0; i < categoryList.size(); i++) {
                int r = i / cols, c = i % cols;
                Label label = new Label(categoryList.get(i).getName());
                label.getStyleClass().add("category-label");
                categoryGrid.add(label, c, r);
                label.setPrefWidth(CATEGORY_CHOICE_WIDTH);
                label.setPrefHeight(CATEGORY_CHOICE_HEIGHT);
                Category category = categoryList.get(i);
                label.setOnMouseClicked(e -> {
                    setCurrentCategory(category);
                    safeSwitchScene("category.fxml");
                });
            }
            categoryGrid.getRowConstraints().clear();
            for (int r = 0; r < rows; r++) {
                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setPrefHeight(CATEGORY_CHOICE_HEIGHT);
                categoryGrid.getRowConstraints().add(rowConstraints);
            }
            DropShadow ds = new DropShadow();
            ds.setRadius(15);
            ds.setOffsetY(5);
            categoryGrid.setEffect(ds);
            Common.disable(categoryPane);
            categoryBtn.setOnMouseEntered(event -> Common.enable(categoryPane));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRedirect() {
        homeBtn.setOnAction((event) -> safeSwitchScene("home.fxml"));
        advancedSearchBtn.setOnAction((event) -> safeSwitchScene("advanced-search.fxml"));
    }

    private void initSearchBox() {
        if (Trie.getInstance().getCnt() == 0) {
            Trie.getInstance().buildTrie();
        }
        Common.disable(suggestionsScrollPane);
        searchBox.setOnKeyReleased(event -> {
            Pair<Trie.Node, Trie.Node> range = Trie.getInstance().getRange(searchBox.getText());
            displaySuggestionPane(range.getKey(), range.getValue());
            Common.enable(suggestionsScrollPane);
            Common.disable(notificationPane);
            Common.disable(userPane);
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
        changePasswordBtn.setOnMouseClicked(event -> safeSwitchScene("change-password.fxml"));
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
                node.setCursor(Cursor.HAND);
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
        userBtn.setCursor(Cursor.HAND);
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
        categoryFu = executor.submit(() -> CategoryQuery.getInstance().getAll());
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
                    Common.disable(suggestionsScrollPane);
                    pane.requestFocus();
                }
            });

            stage.getScene().setOnMouseMoved(e -> {
                if (!categoryBtn.localToScene(categoryBtn.getBoundsInLocal()).contains(e.getSceneX(), e.getSceneY())
                && !categoryPane.getBoundsInParent().contains(e.getX(), e.getY())
                && !categoryPane.isDisable()) {
                    Common.disable(categoryPane);
                }
            });

            categoryBtn.setOnMouseExited(e -> {
                if (!categoryPane.localToScene(categoryPane.getBoundsInLocal()).contains(e.getSceneX(), e.getSceneY()) && !categoryPane.isDisable()) {
                    Common.disable(categoryPane);
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
            button.setStyle("-fx-background-color: #FFFFFF;");
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
            button.setStyle("-fx-background-color: #FFFFFF;");
            button.setCursor(Cursor.HAND);
            button.setPrefWidth(460);
            button.setAlignment(Pos.CENTER_LEFT);
            button.setOnAction(event -> {
                Button clickedButton = (Button) event.getSource();
                int id = (Integer)clickedButton.getUserData();
                setCurrentDocument(DocumentQuery.getInstance().getById(id));
                safeSwitchScene("document-detail.fxml");
                //System.out.println("On clicked");
            });
            button.setOnMouseEntered(e -> {
                button.setStyle("-fx-background-color: #5C1C0033;");
            });
            button.setOnMouseExited(e -> {
                button.setStyle("-fx-background-color: #FFF;");
            });
            suggestionsBox.getChildren().add(button);
            size ++;
            if (first == last) break;
            first = first.getNex();
        }
//        buttonH += 10;
        suggestionsBox.setPrefHeight(size * buttonH + 10);
        suggestionsScrollPane.setPrefHeight(Math.min(size * buttonH + 10, 300));
    }
}