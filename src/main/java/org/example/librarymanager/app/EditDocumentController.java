package org.example.librarymanager.app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.ButtonDialog;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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
    private ComboBox<Common.Choice> docCategories;
    @FXML
    private Label submitMessage;

    private Document document;

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

        document = getCurrentDocument();
        docTitle.setText(document.getTitle());
        docAuthor.setText(document.getAuthor());
        docDescription.setText(document.getDescription());
        docImageLink.setText(document.getImageLink());
        docQuantity.setText(String.valueOf(document.getQuantity()));
        Optional<Common.Choice> opt = docCategories.getItems().stream().filter(cat -> cat.getValue() ==  document.getCategoryId()).findFirst();
        if (opt.isPresent()) {
            docCategories.getSelectionModel().select(opt.get());
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

        if (docAuthor.getText().isEmpty()) {
            docAuthor.setText(getUser().getFirstname() + " " + getUser().getLastname());
        }

        if (!submitMessage.getText().isEmpty()) {
            submitMessage.setTextFill(Color.RED);
        }
        else {
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

            if (DocumentQuery.getInstance().update(document)) {
                submitMessage.setText("Successfully updated!");
                submitMessage.setTextFill(Color.GREEN);
            }
            else {
                submitMessage.setText("Some errors occurred! Please try again!");
                submitMessage.setTextFill(Color.RED);
            }
        }

        submitMessage.setVisible(true);
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
