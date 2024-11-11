package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.ButtonDialog;
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

public class EditDocumentController extends ControllerWrapper {
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
    private MFXComboBox<Common.Choice> docCategories;
    @FXML
    private Label submitMessage;
    @FXML
    private MFXProgressSpinner loader;
    @FXML
    private MFXButton uploadBtn;
    @FXML
    private Label uploadlabel;
    @FXML
    private MFXButton updateBtn;
    @FXML
    private MFXButton cancelBtn;
    @FXML
    private MFXButton deleteBtn;

    private Document document;
    private File imageFile;

    /**
     * Hide message when edit input.
     * @param o text field object
     * @param message label object
     */
    private void hideMessage(Object o, Object message) {
        if (o instanceof TextField && message instanceof Label) {
            ((TextField) o).textProperty().addListener((observable, oldValue, newValue) -> {
                ((Label) message).setVisible(false);
            });
        }
    }

    /**
     * Initialization.
     * Create a scene display all details of current document for editing.
     * @param location url to edit-document.fxml
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Category> categories = CategoryQuery.getInstance().getAll();
        for (Category category : categories) {
            docCategories.getItems().add(new Common.Choice(category.getId(), category.getName()));
        }
        submitMessage.setVisible(false);

        hideMessage(docTitle, submitMessage);
        hideMessage(docQuantity, submitMessage);
        hideMessage(docDescription, submitMessage);
        hideMessage(docAuthor, submitMessage);

        Common.disable(loader);

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

        document = getCurrentDocument();
        docTitle.setText(document.getTitle());
        docAuthor.setText(document.getAuthor());
        docDescription.setText(document.getDescription());
        docImageLink.setText(document.getImageLink());
        docQuantity.setText(String.valueOf(document.getQuantity()));
        Optional<Common.Choice> opt = docCategories.getItems().stream().filter(cat -> cat.getValue() ==  document.getCategoryId()).findFirst();
        if (opt.isPresent()) {
            docCategories.getSelectionModel().selectItem(opt.get());
        } else {
            docCategories.getSelectionModel().selectLast();
        }
    }

    /**
     * Update button on action.
     * Submit message handles cases.
     */
    @FXML
    private void handleSubmit() {
        submitMessage.setText("");
        if (docQuantity.getText().isEmpty() || !Common.isInteger(docQuantity.getText())) {
            submitMessage.setText("Please enter a valid quantity!");
        } else {
            if (document.getQuantityInStock() < document.getQuantity() - Integer.parseInt(docQuantity.getText())) {
                submitMessage.setText(
                        "New quantity is currently invalid because there only "
                        + document.getQuantityInStock() + "book"
                        + (document.getQuantityInStock() == 0 ? "" : "s")
                        + " in stock now"
                );
            }
        }
        if (docTitle.getText().isEmpty()) {
            submitMessage.setText("Please enter a title!");
        }
        if (docCategories.getSelectionModel().getSelectedItem() == null) {
            submitMessage.setText("Please select a category!");
        }

        if (!submitMessage.getText().isEmpty()) {
            submitMessage.setTextFill(Color.RED);
            submitMessage.setVisible(true);
        }
        else {
            if (docAuthor.getText().isEmpty()) {
                docAuthor.setText(getUser().getFirstname() + " " + getUser().getLastname());
            }
            Common.disable(updateBtn);
            Common.disable(cancelBtn);
            Common.disable(deleteBtn);
            Common.enable(loader);

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    document.setCategoryId(docCategories.getSelectionModel().getSelectedItem().getValue());
                    document.setAuthor(docAuthor.getText());
                    document.setTitle(docTitle.getText());
                    document.setDescription(docDescription.getText());
                    document.setImageLink(docImageLink.getText());
                    document.setQuantityInStock(
                            document.getQuantityInStock()
                                    - document.getQuantity()
                                    + Integer.parseInt(docQuantity.getText())
                    );
                    document.setQuantity(Integer.parseInt(docQuantity.getText()));
                    if (imageFile != null) {
                        document.setImageLink(Backblaze.getInstance().upload(String.format("document-%s-cover.png", UUID.randomUUID()), imageFile.getAbsolutePath()));
                    }
                    return DocumentQuery.getInstance().update(document);
                }
            };
            task.setOnSucceeded(e -> {
                if (task.getValue()) {
                    submitMessage.setText("Successfully updated!");
                    submitMessage.getStyleClass().clear();
                    submitMessage.getStyleClass().add("form-message--success");
                } else {
                    submitMessage.setText("Some errors occurred! Please try again!");
                    submitMessage.getStyleClass().clear();
                    submitMessage.getStyleClass().add("form-message--error");
                }
                submitMessage.setVisible(true);
                Common.enable(updateBtn);
                Common.enable(cancelBtn);
                Common.enable(deleteBtn);
                Common.disable(loader);
            });
            new Thread(task).start();
        }
    }

    /**
     * Cancel button on action.
     * Switch scene to previous scene.
     */
    @FXML
    private void handleCancel() {
        backScene();
    }

    /**
     * Delete button on action.
     * Dialog appears to confirm delete action.
     * Switch scene to home.
     */
    @FXML
    private void handleDelete() {
        ButtonDialog deleteDialog = new ButtonDialog(stage,
                "Delete Document",
                "Are you sure you want delete this document ?",
                "This action can't be reverted",
                true
        );
        Optional<ButtonType> result = deleteDialog.getDialog().showAndWait();
        if (result.isPresent() && result.get() == deleteDialog.getCancelButton()) {
            return;
        }

        String message = "";
        if (DocumentQuery.getInstance().delete(document)) {
            message = "Successfully deleted!";
        } else {
            message = "Some errors occurred! Please try again!";
        }
        ButtonDialog dialog = new ButtonDialog(stage, "Delete Document", message, "", false);
        dialog.getDialog().showAndWait();

        setCurrentDocument(null);
        safeSwitchScene("home.fxml");
    }
}
