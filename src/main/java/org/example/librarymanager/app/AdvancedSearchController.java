package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.checkerframework.checker.units.qual.C;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdvancedSearchController extends ControllerWrapper {
    public static final int SORT_BY_NONE = 0;
    public static final int SORT_BY_TITLE_ASC = 1;
    public static final int SORT_BY_TITLE_DESC = 2;
    public static final int SORT_BY_RATE_ASC = 3;
    public static final int SORT_BY_RATE_DESC = 4;
    public static final int SORT_BY_DATE_ASC = 5;
    public static final int SORT_BY_DATE_DESC = 6;

    public static final int FILTER_STATUS_ANY = 0;
    public static final int FILTER_STATUS_REMAIN = 1;
    public static final int FILTER_STATUS_NO_REMAIN = 2;

    @FXML
    private TextField searchBox;
    @FXML
    private HBox searchBoxContainer;
    @FXML
    private VBox documentsContainer;
    @FXML
    private MFXComboBox<Common.Choice> sortByFilter;
    @FXML
    private MFXComboBox<Common.Choice> categoryFilter;
    @FXML
    private MFXComboBox<Common.Choice> statusFilter;
    @FXML
    private MFXButton searchButton;

    private List<Document> documents;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchBoxContainer.getStyleClass().add("search-box-container--focused");
            }
            else {
                searchBoxContainer.getStyleClass().remove("search-box-container--focused");
            }
        });

        sortByFilter.getItems().addAll(
                new Common.Choice(SORT_BY_NONE, "None"),
                new Common.Choice(SORT_BY_TITLE_ASC, "Title Ascending"),
                new Common.Choice(SORT_BY_TITLE_DESC, "Title Descending"),
                new Common.Choice(SORT_BY_RATE_DESC, "Highest Rating"),
                new Common.Choice(SORT_BY_RATE_ASC, "Lowest Rating"),
                new Common.Choice(SORT_BY_DATE_DESC, "Recently Added"),
                new Common.Choice(SORT_BY_DATE_ASC, "Oldest Added")
        );

        Task<List<Category>> task = new Task<List<Category>>() {
            @Override
            protected List<Category> call() throws Exception {
                return CategoryQuery.getInstance().getAll();
            }
        };
        task.setOnSucceeded((e -> {
            List<Category> categories = task.getValue();
            categoryFilter.getItems().clear();
            for (Category category : categories) {
                categoryFilter.getItems().add(new Common.Choice(category.getId(), category.getName()));
            }
        }));
        new Thread(task).start();

        statusFilter.getItems().addAll(
                new Common.Choice(FILTER_STATUS_ANY, "Any"),
                new Common.Choice(FILTER_STATUS_REMAIN, "Still remain"),
                new Common.Choice(FILTER_STATUS_NO_REMAIN, "None remain")
        );

        sortByFilter.getSelectionModel().selectFirst();
        categoryFilter.getSelectionModel().selectFirst();
        statusFilter.getSelectionModel().selectFirst();

        searchButton.setOnAction(e -> {

        });
    }
}
