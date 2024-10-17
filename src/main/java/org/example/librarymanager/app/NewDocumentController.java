package org.example.librarymanager.app;

import com.google.api.services.books.model.Volume;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class NewDocumentController extends ControllerWrapper {
    @FXML
    private TextField docTitle;
    @FXML
    private TextField docAuthor;
    @FXML
    private TextArea docDescription;
    @FXML
    private TextField docImageLink;
    @FXML
    private TextField docQuantity;
    @FXML
    private ComboBox<Common.Choice> docCategories;
    @FXML
    private Button docSubmit;

    @FXML
    private TextField docISBN;
    @FXML
    private Button docSearch;
    @FXML
    private Label searchMessage;
    @FXML
    private Label submitMessage;

    private void hideMessage(Object o, Object message) {
        if (o instanceof TextField && message instanceof Label) {
            ((TextField) o).textProperty().addListener((observable, oldValue, newValue) -> {
                ((Label) message).setVisible(false);
            });
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Category> categories = CategoryQuery.getCategories();
        for (Category category : categories) {
            docCategories.getItems().add(new Common.Choice(category.getId(), category.getName()));
        }
        searchMessage.setVisible(false);
        submitMessage.setVisible(false);

        hideMessage(docTitle, submitMessage);
        hideMessage(docQuantity, submitMessage);
        hideMessage(docISBN, submitMessage);
        hideMessage(docDescription, submitMessage);
        hideMessage(docAuthor, submitMessage);

        hideMessage(docISBN, searchMessage);
    }

    @FXML
    private void onSearchByISBN(ActionEvent event) {
        Volume volume = DocumentQuery.getDocumentByISBN(docISBN.getText());
        if (volume != null) {
            searchMessage.setText("Searching complete!");
            searchMessage.setTextFill(Color.GREEN);
            docTitle.setText(volume.getVolumeInfo().getTitle());
            docAuthor.setText(String.join(", ", volume.getVolumeInfo().getAuthors()));
            docDescription.setText(volume.getVolumeInfo().getDescription());
            docImageLink.setText(volume.getVolumeInfo().getImageLinks().getThumbnail());
            String category = volume.getVolumeInfo().getCategories() != null ? volume.getVolumeInfo().getCategories().getFirst() : "other";
            Optional<Common.Choice> opt = docCategories.getItems().stream().filter(cat -> cat.getLabel().equals(category)).findFirst();
            if (opt.isPresent()) {
                docCategories.getSelectionModel().select(opt.get());
            } else {
                docCategories.getSelectionModel().selectLast();
            }
        } else {
            searchMessage.setText("ISBN not found!");
            searchMessage.setTextFill(Color.RED);
        }
        searchMessage.setVisible(true);
    }

    @FXML
    private void handleSubmit() {
        submitMessage.setText("");
        if (docQuantity.getText().isEmpty() || !Common.isInteger(docQuantity.getText())) {
            submitMessage.setText("Please enter a valid quantity!");
        }
        if (docTitle.getText().isEmpty()) {
            submitMessage.setText("Please enter a title!");
        }
        if (docCategories.getSelectionModel().getSelectedItem() == null) {
            submitMessage.setText("Please select a category!");
        }

        if (docAuthor.getText().isEmpty()) {
            docAuthor.setText(getUser().getFirstname() + " " + getUser().getLastname());
        }

        if (!submitMessage.getText().isEmpty()) {
            submitMessage.setTextFill(Color.RED);
        }
        else {
            Document document = DocumentQuery.addDocument(docCategories.getSelectionModel().getSelectedItem().getValue(),
                    getUser().getId(),
                    docAuthor.getText(),
                    docTitle.getText(),
                    docDescription.getText(),
                    docImageLink.getText(),
                    Integer.parseInt(docQuantity.getText())
            );
            if (document != null) {
                docTitle.clear();
                docAuthor.clear();
                docDescription.clear();
                docImageLink.clear();
                docQuantity.clear();
                docCategories.getSelectionModel().clearSelection();

                submitMessage.setText("Successfully added!");
                submitMessage.setTextFill(Color.GREEN);
            }
            else {
                submitMessage.setText("Some errors occurred! Please try again!");
                submitMessage.setTextFill(Color.RED);
            }
        }

        submitMessage.setVisible(true);
    }
}
