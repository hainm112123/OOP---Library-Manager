package org.example.librarymanager.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.data.Trie;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    TextField searchBox;
    @FXML
    VBox suggestionsBox;
    @FXML
    ScrollPane suggestionsScrollPane;

    /**
     * Initialization.
     * Create layout with buttons to switch to main scenes.
     * @param location url to top bar.fxml
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topbarHomeBtn.setOnAction((event) -> safeSwitchScene("home.fxml"));
        List<String> categoryList = CategoryQuery.getInstance().getCategoriesName();
        ObservableList<String> list = FXCollections.observableArrayList(categoryList);
        topbarCategoryBtn.setItems(list);
        topbarCategoryBtn.setOnAction((event) -> {
            ControllerWrapper.setCurrentCategory(topbarCategoryBtn.getValue());
            safeSwitchScene("category.fxml");
        });

        topbarDocModifyBtn.setOnAction((event) -> safeSwitchScene("new-document.fxml"));
        topbarProfileBtn.setOnAction((event) -> safeSwitchScene("profile.fxml"));
        topbarMyDocBtn.setOnAction((event) -> safeSwitchScene("my-documents.fxml"));

        if (Trie.getCnt() == 0) {
            Trie.buildTrie();
        }
        suggestionsScrollPane.setVisible(false);
        searchBox.setOnKeyReleased(event -> {
            Pair<Trie.Node, Trie.Node> range = Trie.getRange(searchBox.getText());
            displaySuggestionPane(range.getKey(), range.getValue());
            suggestionsScrollPane.setVisible(true);
//            System.out.println(searchBox.getText());
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