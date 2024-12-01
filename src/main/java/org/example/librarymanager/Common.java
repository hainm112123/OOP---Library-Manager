package org.example.librarymanager;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Comparator;

public class Common {
    public static final String PRIMARY_COLOR = "#5C1C00";
    public static final String TOPBAR_DROPDOWN_BUTTON_BG = "#f0f0f0";
    public static final String TOPBAR_DROPDOWN_BUTTON_BG_HOVER = " #00000011";
    public static Image NO_IMAGE;
    public static Image DEFAULT_PROFILE;

    static {
        try {
            NO_IMAGE = new Image("file:src/main/resources/org/example/librarymanager/image/no_image.jpg");
            DEFAULT_PROFILE = new Image("file:src/main/resources/org/example/librarymanager/image/UserProfile.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ImageView loadImage(ImageView imageView, int width, int height, String imageLink) {
        imageView.setImage(Common.NO_IMAGE);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        Task<Image> imgTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try {
                    return new Image(imageLink);
                } catch (Exception e) {
                    return Common.NO_IMAGE;
                }
            }
        };
        imgTask.setOnSucceeded(e -> {
            imageView.setImage(imgTask.getValue());
        });
        new Thread(imgTask).start();
        return imageView;
    }

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
    @AllArgsConstructor
    public static class StrChoice {
        private String value;
        private String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @Data
    @AllArgsConstructor
    public static class Item<T extends Comparable<T>, V extends Comparable<V>> implements Comparable<Item<T, V>> {
        private T key;
        private V value;

        @Override
        public int compareTo(Item<T, V> other) {
            return key.compareTo(other.key);
        }

        public static class compareByValue<T extends Comparable<T>, V extends Comparable<V>> implements Comparator<Item<T, V>> {
            @Override
            public int compare(Item<T, V> o1, Item<T, V> o2) {
                return o1.value.compareTo(o2.value);
            }
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
     * hide and disable node without remove it from layout calculations.
     * @param node
     */
    public static void hide(Node node) {
        node.setDisable(true);
        node.setVisible(false);
    }

    /**
     * show hided node back.
     * @param node
     */
    public static void show(Node node) {
        node.setDisable(false);
        node.setVisible(true);
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

    /**
     * hash input string and take first len characters.
     * @param input
     * @param len
     * @return
     */
    public static String hashString(String input, int len) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            String result = "";
            for (byte b : hash) {
                result += String.format("%02x", b);
            }
            return len > result.length() ? result: result.substring(0, len);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
