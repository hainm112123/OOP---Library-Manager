package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.NotificationComponent;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.data.Trie;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TopbarController extends ControllerWrapper {
    @FXML
    Button topbarHomeBtn;
    @FXML
    private ComboBox<String> topbarCategoryBtn;
    @FXML
    Button topbarDocModifyBtn;
    @FXML
    Button topbarProfileBtn;
    @FXML
    Button topbarMyDocBtn;
    @FXML
    Button topbarBorrowDocBtn;
    @FXML
    TextField searchBox;
    @FXML
    VBox suggestionsBox;
    @FXML
    ScrollPane suggestionsScrollPane;
    @FXML
    StackPane pane;
    @FXML
    ImageView notificationBell;
    @FXML
    Label notificationBadge;
    @FXML
    MFXScrollPane notificationPane;
    @FXML
    VBox notificationBox;

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
        topbarDocModifyBtn.setOnAction((event) -> safeSwitchScene("new-document.fxml"));
        topbarProfileBtn.setOnAction((event) -> safeSwitchScene("profile.fxml"));
        topbarMyDocBtn.setOnAction((event) -> safeSwitchScene("my-documents.fxml"));
        topbarBorrowDocBtn.setOnAction((event) -> safeSwitchScene("borrowing-documents.fxml"));
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
        Platform.runLater(() -> {
            getStage().getScene().setOnMousePressed(event -> {
                if (!searchBox.isFocused()) {
                    return;
                }
                if (!searchBox.getBoundsInParent().contains(event.getX(), event.getY())) {
                    suggestionsScrollPane.setVisible(false);
                    pane.requestFocus();
                }
            });
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
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
                notificationBox.getChildren().add(new NotificationComponent(document, this).getContainer());
            }
            notificationBell.setOnMouseClicked(event -> {
               Common.enable(notificationPane);
            });
            DropShadow ds = new DropShadow();
            ds.setRadius(10);
            notificationPane.setEffect(ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        initCategory();
        initRedirect();
        initNotification();
        initSearchBox();
        executor.shutdown();
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