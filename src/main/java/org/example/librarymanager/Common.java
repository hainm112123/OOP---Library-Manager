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
}
