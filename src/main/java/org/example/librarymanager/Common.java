package org.example.librarymanager;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;

public class Common {
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    public static class Choice {
        private int value;
        private String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @Data
    public static class ButtonDialog {
        private Dialog<ButtonType> dialog;
        private ButtonType okButton;
        private ButtonType cancelButton;

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
}
