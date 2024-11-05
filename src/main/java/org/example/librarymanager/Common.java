package org.example.librarymanager;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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

    public static void disable(Node node) {
        node.setDisable(true);
        node.setVisible(false);
    }

    public static void enable(Node node) {
        node.setDisable(false);
        node.setVisible(true);
    }
}
