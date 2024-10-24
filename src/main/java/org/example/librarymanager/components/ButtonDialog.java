package org.example.librarymanager.components;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Data;

@Data
public class ButtonDialog {
    private Dialog<ButtonType> dialog;
    private ButtonType okButton;
    private ButtonType cancelButton;

    /**
     * Construct a dialog to ask app user.
     * @param owner owner stage
     * @param title dialog title
     * @param header dialog header
     * @param content dialog content
     * @param cancellable can dialog canceled
     */
    public ButtonDialog(Stage owner, String title, String header, String content, boolean cancellable) {
        dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setContentText(content);
        okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        if (cancellable) {
            cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(cancelButton);
        }
    }
}