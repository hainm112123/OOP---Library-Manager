package org.example.librarymanager;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;

public class Common {
    public static final String PRIMARY_COLOR = "#5C1C00";
    public static final String TOPBAR_DROPDOWN_BUTTON_BG = "#f0f0f0";
    public static final String TOPBAR_DROPDOWN_BUTTON_BG_HOVER = " #00000011";

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

    /**
     * disable node, make it invisible and remove from layout calculations
     * (make sure it doesn't occupy space or block mouse event)
     * @param node
     */
    public static void disable(Node node) {
        node.setDisable(true);
        node.setVisible(false);
        node.setMouseTransparent(true);
        node.setManaged(false);
    }

    /**
     * enable node, make it visible and add to layout calculations
     * @param node
     */
    public static void enable(Node node) {
        node.setDisable(false);
        node.setVisible(true);
        node.setMouseTransparent(false);
        node.setManaged(true);
    }

    /**
     * Hide message when edit input.
     * @param o text field object
     * @param message label object
     */
    public static void hideMessage(Object o, Object message) {
        if (o instanceof MFXTextField && message instanceof Label) {
            ((MFXTextField) o).textProperty().addListener((observable, oldValue, newValue) -> {
                ((Label) message).setVisible(false);
            });
        }
        if (o instanceof MFXPasswordField && message instanceof Label) {
            ((MFXPasswordField) o).textProperty().addListener((observable, oldValue, newValue) -> {
                ((Label) message).setVisible(false);
            });
        }
    }
}
