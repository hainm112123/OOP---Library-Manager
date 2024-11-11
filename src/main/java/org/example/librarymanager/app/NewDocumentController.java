package org.example.librarymanager.app;

import com.google.api.services.books.model.Volume;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Document;

import javax.print.Doc;
import javax.swing.text.DocumentFilter;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewDocumentController extends ControllerWrapper {
    @FXML
    private TextField docTitle;
    @FXML
    private MFXTextField docAuthor;
    @FXML
    private TextArea docDescription;
    @FXML
    private MFXTextField docImageLink;
    @FXML
    private MFXTextField docQuantity;
    @FXML
    private MFXComboBox<Common.Choice> docCategories;
    @FXML
    private MFXButton docSubmit;

    @FXML
    private MFXTextField docISBN;
    @FXML
    private MFXButton docSearch;
    @FXML
    private Label searchMessage;
    @FXML
    private Label submitMessage;

    @FXML
    private MFXButton uploadBtn;
    @FXML
    private Label uploadlabel;
    @FXML
    private MFXProgressSpinner loader;

    private File imageFile;

    /**
     * Hide message when edit input.
     * @param o text field object
     * @param message label object
     */
    private void hideMessage(Object o, Object message) {
        if (o instanceof MFXTextField && message instanceof Label) {
            ((MFXTextField) o).textProperty().addListener((observable, oldValue, newValue) -> {
                ((Label) message).setVisible(false);
            });
        }
    }

    /**
     * Initialize forms.
     * Search by Google Books API or manually add a new book.
     * @param location url to new-document.fxml
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Category> categories = CategoryQuery.getInstance().getAll();
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

        Common.disable(loader);

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            imageFile = fileChooser.showOpenDialog(stage);
            if (imageFile != null) {
                uploadlabel.setText(imageFile.getAbsolutePath());
            } else {
                uploadlabel.setText("No file chosen");
            }
        });
    }

    /**
     * Search book by ISBN and fill in the form.
     */
    @FXML
    private void onSearchByISBN(ActionEvent event) {
        Volume volume = DocumentQuery.getInstance().getDocumentByISBN(docISBN.getText());
        if (volume != null) {
            searchMessage.setText("Searching complete!");
//            searchMessage.setTextFill(Color.GREEN);
            docTitle.setText(volume.getVolumeInfo().getTitle());
            docAuthor.setText(String.join(", ", volume.getVolumeInfo().getAuthors()));
            docDescription.setText(volume.getVolumeInfo().getDescription());
            if (volume.getVolumeInfo().getImageLinks() != null) {
                docImageLink.setText(volume.getVolumeInfo().getImageLinks().getThumbnail());
            }
            String category = volume.getVolumeInfo().getCategories() != null ? volume.getVolumeInfo().getCategories().getFirst() : "other";
            Optional<Common.Choice> opt = docCategories.getItems().stream().filter(cat -> cat.getLabel().equals(category)).findFirst();
            if (opt.isPresent()) {
                docCategories.getSelectionModel().selectItem(opt.get());
            } else {
                docCategories.getSelectionModel().selectLast();
            }
            searchMessage.getStyleClass().clear();
            searchMessage.getStyleClass().add("form-message--success");
        } else {
            searchMessage.setText("ISBN not found!");
//            searchMessage.setTextFill(Color.RED);
            searchMessage.getStyleClass().clear();
            searchMessage.getStyleClass().add("form-message--error");
        }
        searchMessage.setVisible(true);
    }

    /**
     * Handle submit cases.
     */
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
            submitMessage.getStyleClass().clear();
            submitMessage.getStyleClass().add("form-message--error");
        }
        else {
            Common.disable(docSubmit);
            Common.enable(loader);
            executor = Executors.newSingleThreadExecutor();
            Future<Document> documentFu = executor.submit(() -> {
                String imageLink = docImageLink.getText();
                if (imageFile != null) {

                }
                return DocumentQuery.getInstance().add(new Document(
                        docCategories.getSelectionModel().getSelectedItem().getValue(),
                        getUser().getId(),
                        docAuthor.getText(),
                        docTitle.getText(),
                        docDescription.getText(),
                        imageLink,
                        Integer.parseInt(docQuantity.getText())
                ));
            });
            executor.shutdown();
            try {
                Document document = documentFu.get();
                Common.enable(docSubmit);
                Common.disable(loader);
                if (document != null) {
                    docTitle.clear();
                    docAuthor.clear();
                    docDescription.clear();
                    docImageLink.clear();
                    docQuantity.clear();
                    docCategories.getSelectionModel().clearSelection();

                    submitMessage.setText("Successfully added!");
                    submitMessage.getStyleClass().clear();
                    submitMessage.getStyleClass().add("form-message--success");
                }
                else {
                    submitMessage.setText("Some errors occurred! Please try again!");
                    submitMessage.getStyleClass().clear();
                    submitMessage.getStyleClass().add("form-message--error");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        submitMessage.setVisible(true);
    }
}
