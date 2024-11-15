package org.example.librarymanager.app;

import javafx.application.Platform;
import org.example.librarymanager.data.RatingQuery;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Rating;

import java.net.URL;
import java.util.ResourceBundle;

public class EditDataRatingController extends EditDataController<Rating> {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        dataAccessObject = RatingQuery.getInstance();
        Platform.runLater(this::enableDelete);
    }
}
