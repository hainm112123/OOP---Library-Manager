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
import org.example.librarymanager.components.Avatar;
import org.example.librarymanager.components.NotificationComponent;
import org.example.librarymanager.data.*;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Notification;
import org.example.librarymanager.models.User;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TopbarController extends ControllerWrapper {
    private static final int CATEGORY_CHOICE_WIDTH = 150;
    private static final int CATEGORY_CHOICE_HEIGHT = 42;
    private static final int USER_MENU_HEIGHT = 560;

    private static final int NOTIFICATION_ALL = 15;
    private static final int NOTIFICATION_OTHER = 1;
    private static final int NOTIFICATION_OVERDUE = 2;
    private static final int NOTIFICATION_WISHLIST = 4;
    private static final int NOTIFICATION_REQUEST = 8;

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
    private HBox searchBoxContainer;
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
    private Label notificationAllBtn;
    @FXML
    private Label notificationOverdueBtn;
    @FXML
    private Label notificationWishlistBtn;
    @FXML
    private Label notificationRequestBtn;
    @FXML
    private Label notificationOtherBtn;
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
    private HBox requestBtn;
    @FXML
    private HBox manageBtn;
    @FXML
    private HBox signoutBtn;
    @FXML
    private ImageView profileImage;

    private Future<List<Category>> categoryFu;
    private Future<List<Document>> overdueDocumentFu;
    private Future<List<Document>> wishlistDocumentFu;
    private Future<Integer> pendingFu;
    private Future<List<Notification>> requestNotificationFu;

    private List<NotificationComponent> overdues;
    private List<NotificationComponent> wishlist;
    private List<NotificationComponent> requests;
    private List<NotificationComponent> others;

    private List<Label> notificationButtons = new ArrayList<>();

    /**
     * init category menu
     */
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

    /**
     * init redirect button
     */
    private void initRedirect() {
        homeBtn.setOnAction((event) -> safeSwitchScene("home.fxml"));
        advancedSearchBtn.setOnAction((event) -> safeSwitchScene("advanced-search.fxml"));
    }

    /**
     * init trie for searching
     */
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

    /**
     * set notification's elements
     * @param type
     */
    private void setNotificationBox(int type) {
        notificationBox.getChildren().clear();
        if ((type & NOTIFICATION_OTHER) != 0) {
            for (NotificationComponent component: others) {
                notificationBox.getChildren().add(component.getElement());
            }
        }
        if ((type & NOTIFICATION_OVERDUE) != 0) {
            for (NotificationComponent component: overdues) {
                notificationBox.getChildren().add(component.getElement());
            }
        }
        if ((type & NOTIFICATION_REQUEST) != 0) {
            for (NotificationComponent component: requests) {
                notificationBox.getChildren().add(component.getElement());
            }
        }
        if ((type & NOTIFICATION_WISHLIST) != 0) {
            for (NotificationComponent component: wishlist) {
                notificationBox.getChildren().add(component.getElement());
            }
        }
        notificationBox.setPrefHeight(notificationBox.getChildren().size() * NotificationComponent.COMPONENT_HEIGHT);
        notificationBox.setMinHeight(notificationBox.getChildren().size() * NotificationComponent.COMPONENT_HEIGHT);
        notificationBox.setMaxHeight(notificationBox.getChildren().size() * NotificationComponent.COMPONENT_HEIGHT);
    }

    /**
     * change type of notification showed.
     * @param activeButton
     * @param notificationButtonStatus
     */
    private void setNotificationButtonStatus(Label activeButton, int notificationButtonStatus) {
        for (Label button: notificationButtons) {
            if (button == activeButton) {
                button.getStyleClass().add("notification-type-button--active");
            } else {
                button.getStyleClass().removeAll("notification-type-button--active");
            }
        }
        setNotificationBox(notificationButtonStatus);
    }

    /**
     * init notification's content
     */
    private void initNotification() {
        Common.disable(notificationPane);
        try {
            List<Document> documents = overdueDocumentFu.get();
            List<Document> wishlistDocuments = wishlistDocumentFu.get();
            List<Notification> requestNotifications = requestNotificationFu.get();
            overdues = new ArrayList<>();
            wishlist = new ArrayList<>();
            requests = new ArrayList<>();
            others = new ArrayList<>();
            if (pendingFu.get() > 0 && getUser().getPermission() != User.TYPE_USER) {
                requests.add(new NotificationComponent(
                        null, null, this,
                        "New borrow requests",
                        String.format("There are %d new borrow requests. Go check them now!", pendingFu.get()),
                        "borrow-request.fxml",
                        notificationPane
                ));
            }
            for (Document document : documents) {
                overdues.add(new NotificationComponent(
                        document, null, this,
                        "You should return this book soon!",
                        "You have borrowed \"" + document.getTitle() + "\" more than 14 days, you should return it soon. Otherwise, you must pay fine due to overdue.",
                        "document-detail.fxml",
                        notificationPane
                ));
            }
            for (Document document: wishlistDocuments) {
                wishlist.add(new NotificationComponent(
                        document, null, this,
                        "A book in your wishlist is now available",
                        "\"" + document.getTitle() + "\" is currently available. You can go borrow it right now!",
                        "document-detail.fxml",
                        notificationPane
                ));
            }
            for (Notification notification: requestNotifications) {
                String type = notification.getType() == Notification.TYPE.REQUEST_APPROVED.ordinal() ? "approved" : "declined";
                requests.add(new NotificationComponent(
                        notification.getDocument(), notification, this,
                        "Your borrow request was " + type,
                        "Your borrow request for \"" + notification.getDocument().getTitle() + "\" was " + type,
                        "document-detail.fxml",
                        notificationPane
                ));
            }

            int size = others.size() + overdues.size() + wishlist.size() + requests.size();
            notificationBadge.setText(String.valueOf(size));
            setNotificationBox(NOTIFICATION_ALL);
            if (size == 0) {
                Common.disable(notificationBadge);
            }
            else {
                Common.enable(notificationBadge);
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

            notificationButtons.add(notificationAllBtn);
            notificationButtons.add(notificationOverdueBtn);
            notificationButtons.add(notificationWishlistBtn);
            notificationButtons.add(notificationRequestBtn);
//            notificationButtons.add(notificationOtherBtn);
            notificationAllBtn.setOnMouseClicked(e -> setNotificationButtonStatus(notificationAllBtn, NOTIFICATION_ALL));
            notificationOverdueBtn.setOnMouseClicked(e -> setNotificationButtonStatus(notificationOverdueBtn, NOTIFICATION_OVERDUE));
            notificationWishlistBtn.setOnMouseClicked(e -> setNotificationButtonStatus(notificationWishlistBtn, NOTIFICATION_WISHLIST));
            notificationRequestBtn.setOnMouseClicked(e -> setNotificationButtonStatus(notificationRequestBtn, NOTIFICATION_REQUEST));
//            notificationOtherBtn.setOnMouseClicked(e -> setNotificationButtonStatus(notificationOtherBtn, NOTIFICATION_OTHER));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init user's menu
     */
    private void initUser() {
        userBtn = (ImageView) new Avatar(userBtn, 48, getUser().getImageLink()).getElement();
        profileImage = (ImageView) new Avatar(profileImage, 48, getUser().getImageLink()).getElement();

        usernameLabel.setText(getUser().getUsername());
        usertypeLabel.setText(User.USER_TYPE_STRING[getUser().getPermission()]);
        try {
            if (requestBtn.getChildren().getLast() instanceof Label) {
                ((Label) requestBtn.getChildren().getLast()).setText(String.format("Borrow requests (%d)", pendingFu.get()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        profileBtn.setOnMouseClicked(event -> {
            Common.disable(userPane);
            safeSwitchScene("profile.fxml");
        });
        changePasswordBtn.setOnMouseClicked(event -> {
            Common.disable(userPane);
            safeSwitchScene("change-password.fxml");
        });
        bookshelfBtn.setOnMouseClicked(event -> {
            Common.disable(userPane);
            safeSwitchScene("bookshelf.fxml");
        });
        mydocBtn.setOnMouseClicked(event -> {
            Common.disable(userPane);
            safeSwitchScene("my-documents.fxml");
        });
        newdocBtn.setOnMouseClicked(event -> {
            Common.disable(userPane);
            safeSwitchScene("new-document.fxml");
        });
        requestBtn.setOnMouseClicked(event -> {
            Common.disable(userPane);
            safeSwitchScene("borrow-request.fxml");
        });
        manageBtn.setOnMouseClicked(event -> {
            Common.disable(userPane);
            safeSwitchScene("admin.fxml");
        });
        signoutBtn.setOnMouseClicked(event -> {
            Common.disable(userPane);
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
        int unavailableRows = 0;
        if (getUser().getPermission() != User.TYPE_ADMIN) {
            userBox.getChildren().remove(manageBtn);
            unavailableRows ++;
//            userBox.getChildren().subList(9, 10).clear();
//            userBox.setPrefHeight(480);
//            userBox.setMaxHeight(480);
        }
        if (getUser().getPermission() == User.TYPE_USER) {
            userBox.getChildren().remove(mydocBtn);
            userBox.getChildren().remove(newdocBtn);
            userBox.getChildren().remove(requestBtn);
            userBox.getChildren().remove(manageBtn);
//            userBox.getChildren().subList(7, 10).clear();
//            userBox.setPrefHeight(400);
//            userBox.setMaxHeight(400);
            unavailableRows += 4;
        }
        userBox.setPrefHeight(USER_MENU_HEIGHT - unavailableRows * 40);
        userBox.setMaxHeight(USER_MENU_HEIGHT - unavailableRows * 40);

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
        searchBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchBoxContainer.getStyleClass().add("search-box-container--focused");
            }
            else {
                searchBoxContainer.getStyleClass().remove("search-box-container--focused");
            }
        });

        executor = Executors.newFixedThreadPool(5);
        categoryFu = executor.submit(() -> CategoryQuery.getInstance().getAll());
        overdueDocumentFu = executor.submit(() -> ServiceQuery.getInstance().getOverdueDocuments(getUser().getId()));
        wishlistDocumentFu = executor.submit(() -> ServiceQuery.getInstance().getWishlistAvailableDocuments(getUser().getId()));
        pendingFu = executor.submit(() -> ServiceQuery.getInstance().getNumberOfPendingServices());
        requestNotificationFu = executor.submit(() -> NotificationQuery.getInstance().getUnreadNotifications(getUser().getId()));
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

    /**
     * show search box suggestions.
     * @param first
     * @param last
     */
    private void displaySuggestionPane(Trie.Node first, Trie.Node last) {
        suggestionsBox.getChildren().clear();
        int buttonH = 40;
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
            button.setPrefWidth(500);
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