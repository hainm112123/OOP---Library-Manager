package org.example.librarymanager.app;

import com.google.api.services.books.model.Volume;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.DocumentComponent;
import org.example.librarymanager.data.Backblaze;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Document;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

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
    private MFXTextField apiPattern;
    @FXML
    private MFXButton apiSearch;
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
    @FXML
    private MFXProgressSpinner searchLoader;
    @FXML
    private MFXComboBox<Common.StrChoice> searchType;
    @FXML
    private VBox modalContainer;
    @FXML
    private AnchorPane modalOverlay;
    @FXML
    private VBox documentListContainer;
    @FXML
    private MFXButton openModalBtn;

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
        hideMessage(apiPattern, submitMessage);
        hideMessage(docDescription, submitMessage);
        hideMessage(docAuthor, submitMessage);

        hideMessage(apiPattern, searchMessage);

        Common.disable(loader);
        Common.disable(searchLoader);
        Common.disable(modalOverlay);

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
            imageFile = fileChooser.showOpenDialog(stage);
            if (imageFile != null) {
                uploadlabel.setText(imageFile.getAbsolutePath());
            } else {
                uploadlabel.setText("No file chosen");
            }
        });

        openModalBtn.setOnAction(e -> {
            Common.enable(modalOverlay);
        });
        DropShadow ds = new DropShadow();
        ds.setRadius(30);
        modalOverlay.setEffect(ds);
        searchType.getItems().addAll(
                new Common.StrChoice("", "Any"),
                new Common.StrChoice("intitle:", "Title"),
                new Common.StrChoice("inauthor:", "Author"),
                new Common.StrChoice("inpublisher:", "Publisher"),
                new Common.StrChoice("subject:", "Subject"),
                new Common.StrChoice("isbn:", "ISBN"),
                new Common.StrChoice("lccn:", "LCCN"),
                new Common.StrChoice("oclc:", "OCLC")
        );
        searchType.getSelectionModel().selectFirst();
        openModalBtn.setOnAction(e -> {
            Common.enable(modalOverlay);
        });
        apiSearch.setOnAction(this::onSearchByISBN);
    }

    /**
     * Search book by ISBN and fill in the form.
     */
    @FXML
    private void onSearchByISBN(ActionEvent event) {
        Common.enable(searchLoader);
        Common.disable(apiSearch);
        Task<List<Volume>> task = new Task<>() {
            @Override
            protected List<Volume> call() throws Exception {
                return DocumentQuery.getInstance().getDocumentsFromAPI(searchType.getSelectionModel().getSelectedItem().getValue(), apiPattern.getText());
            }
        };
        task.setOnSucceeded((e) -> {
            List<Volume> volumes = task.getValue();
            if (volumes != null && !volumes.isEmpty()) {
                documentListContainer.setPrefHeight(DocumentComponent.DOC_COMPONENT_HEIGHT_DETAIL * volumes.size());
                searchMessage.setText("Searching complete!");
                for (Volume volume : volumes) {
                    Document document = new Document(volume);
                    DocumentComponent component = new DocumentComponent(document, this, DocumentComponent.VIEW_TYPE_DETAIL);
                    documentListContainer.getChildren().add(component.getElement());
                    component.setSelectButtonAction(actionEvent -> {
                        docTitle.setText(volume.getVolumeInfo().getTitle());
                        if (volume.getVolumeInfo().getAuthors() != null) {
                            docAuthor.setText(String.join(", ", volume.getVolumeInfo().getAuthors()));
                        } else {
                            docAuthor.setText("");
                        }
                        docDescription.setText(volume.getVolumeInfo().getDescription());
                        if (volume.getVolumeInfo().getImageLinks() != null) {
                            docImageLink.setText(volume.getVolumeInfo().getImageLinks().getThumbnail());
                        }
                        String category = volume.getVolumeInfo().getCategories() != null ? volume.getVolumeInfo().getCategories().getFirst() : "other";
                        Optional<Common.Choice> opt = docCategories.getItems().stream().filter(cat -> cat.getLabel().equals(category)).findFirst();
                        if (opt.isPresent()) {
                            docCategories.getSelectionModel().selectItem(opt.get());
                        } else {
                            docCategories.getSelectionModel().selectFirst();
                        }
                        Common.disable(modalOverlay);
                    });
                }
                searchMessage.getStyleClass().clear();
                searchMessage.getStyleClass().add("form-message--success");
            } else {
                searchMessage.setText("No document found!");
                searchMessage.getStyleClass().clear();
                searchMessage.getStyleClass().add("form-message--error");
            }
            searchMessage.setVisible(true);
            Common.disable(searchLoader);
            Common.enable(apiSearch);
        });
        new Thread(task).start();
    }

    /**
     * Handle submit cases.
     */
    @FXML
    private void handleSubmit() {
        submitMessage.setText("");
        submitMessage.setVisible(true);
        submitMessage.getStyleClass().clear();
        submitMessage.getStyleClass().add("form-message--error");
        if (docQuantity.getText().isEmpty() || !Common.isInteger(docQuantity.getText())) {
            submitMessage.setText("Please enter a valid quantity!");
            return;
        }
        if (docTitle.getText().isEmpty()) {
            submitMessage.setText("Please enter a title!");
            return;
        }
        if (docCategories.getSelectionModel().getSelectedItem() == null) {
            submitMessage.setText("Please select a category!");
            return;
        }

        if (docAuthor.getText().isEmpty()) {
            docAuthor.setText(getUser().getFirstname() + " " + getUser().getLastname());
        }

        Common.disable(docSubmit);
        Common.enable(loader);
        Task<Document> task = new Task<Document>() {
            @Override
            protected Document call() throws Exception {
                String imageLink = docImageLink.getText();
                if (imageFile != null) {
                    imageLink = Backblaze.getInstance().upload(String.format("document-%s-cover.png", UUID.randomUUID()), imageFile.getAbsolutePath());
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
            }
        };
        task.setOnSucceeded((e) -> {
            Document document = task.getValue();
            if (document != null) {
                docTitle.clear();
                docAuthor.clear();
                docDescription.clear();
                docImageLink.clear();
                docQuantity.clear();
                docCategories.getSelectionModel().clearSelection();
                imageFile = null;
                uploadlabel.setText("No file chosen");

                submitMessage.setVisible(true);
                submitMessage.setText("Successfully added!");
                submitMessage.getStyleClass().clear();
                submitMessage.getStyleClass().add("form-message--success");
            } else {
                submitMessage.setVisible(true);
                submitMessage.setText("Some errors occurred! Please try again!");
                submitMessage.getStyleClass().clear();
                submitMessage.getStyleClass().add("form-message--error");
            }
            Common.enable(docSubmit);
            Common.disable(loader);
        });
        new Thread(task).start();
    }
}
