package org.example.librarymanager.app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.models.Category;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateDocumentController extends ControllerWrapper {
    @Data
    @AllArgsConstructor
    static class Choice {
        private int value;
        private String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @FXML
    private TextField updDocTitle;
    @FXML
    private TextField updDocAuthor;
    @FXML
    private TextArea updDocDescription;
    @FXML
    private TextField updDocQuantity;
    @FXML
    private ComboBox<Choice> updDocCategories;
    @FXML
    private Button updDocSubmit;
    @FXML
    private Label updDocTypeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Category> categories = CategoryQuery.getCategories();
        for (Category category : categories) {
            updDocCategories.getItems().add(new Choice(category.getId(), category.getName()));
        }
    }
}
