package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.controlsfx.control.Rating;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.DocumentQuery;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RatingController extends ControllerWrapper {
    @FXML
    private Rating rating;
    @FXML
    private TextArea content;
    @FXML
    private MFXProgressSpinner loader;
    @FXML
    private MFXButton submitButton;
    @FXML
    private Label ratingLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Common.disable(loader);
        ratingLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 800;");
    }

    @FXML
    private void post() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return DocumentQuery.getInstance().rateDocument(getUser().getId(), getCurrentDocument().getId(), rating.getRating(), content.getText());
            }
        };
        Common.enable(loader);
        Common.disable(submitButton);
        new Thread(task).start();
        task.setOnSucceeded(e -> {
            Common.disable(loader);
            Common.enable(submitButton);
            backScene();
        });
    }
}
