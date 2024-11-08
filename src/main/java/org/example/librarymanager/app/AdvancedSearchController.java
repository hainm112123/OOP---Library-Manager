package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.checkerframework.checker.units.qual.C;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.ListDocumentsComponent;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AdvancedSearchController extends ControllerWrapper {
    public static final int SORT_BY_NONE = 0;
    public static final int SORT_BY_TITLE_ASC = 1;
    public static final int SORT_BY_TITLE_DESC = 2;
    public static final int SORT_BY_RATE_ASC = 3;
    public static final int SORT_BY_RATE_DESC = 4;
    public static final int SORT_BY_DATE_ASC = 5;
    public static final int SORT_BY_DATE_DESC = 6;
    public static final int SORT_BY_BORROWED_ASC = 7;
    public static final int SORT_BY_BORROWED_DESC = 8;

    public static final int FILTER_STATUS_ANY = 0;
    public static final int FILTER_STATUS_REMAIN = 1;
    public static final int FILTER_STATUS_NO_REMAIN = 2;

    public static final int FILTER_CATEGORY_ANY = -1;

    @FXML
    private MFXScrollPane scrollPane;
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
    private List<Category> categories;

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
                new Common.Choice(SORT_BY_DATE_ASC, "Oldest Added"),
                new Common.Choice(SORT_BY_BORROWED_DESC, "Most Borrowed times"),
                new Common.Choice(SORT_BY_BORROWED_ASC, "Least Borrowed times")
        );
        statusFilter.getItems().addAll(
                new Common.Choice(FILTER_STATUS_ANY, "Any"),
                new Common.Choice(FILTER_STATUS_REMAIN, "Still remain"),
                new Common.Choice(FILTER_STATUS_NO_REMAIN, "None remain")
        );

        executor = Executors.newFixedThreadPool(2);
        Future<List<Category>> catFu = executor.submit(() -> CategoryQuery.getInstance().getAll());
        Future<List<Document>> docFu = executor.submit(() -> DocumentQuery.getInstance().getAllWithFullInformation());
        try {
            categories = catFu.get();
            documents = docFu.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        categoryFilter.getItems().add(new Common.Choice(FILTER_CATEGORY_ANY, "Any"));
        for (Category category : categories) {
            categoryFilter.getItems().add(new Common.Choice(category.getId(), category.getName()));
        }

        sortByFilter.getSelectionModel().selectFirst();
        categoryFilter.getSelectionModel().selectFirst();
        statusFilter.getSelectionModel().selectFirst();

        documentsContainer.getChildren().clear();
        documentsContainer.getChildren().add(new ListDocumentsComponent(documents, scrollPane, this).getContainer());

        searchButton.setOnAction(e -> search());
    }

    private void search() {
        List<Document> result = documents;
        int categoryId = categoryFilter.getSelectionModel().getSelectedItem().getValue();
        if (categoryId != FILTER_CATEGORY_ANY) {
            result = result.stream().filter(document -> document.getCategoryId() == categoryId).toList();
        }
        int status = statusFilter.getSelectionModel().getSelectedItem().getValue();
        if (status == FILTER_STATUS_REMAIN) {
            result = result.stream().filter(document -> document.getQuantityInStock() > 0).toList();
        }
        if (status == FILTER_STATUS_REMAIN) {
            result = result.stream().filter(document -> document.getQuantityInStock() == 0).toList();
        }
        int order = sortByFilter.getSelectionModel().getSelectedItem().getValue();
        switch (order) {
            case SORT_BY_TITLE_ASC: {
                Collections.sort(result, new Document.SortByTitle());
                break;
            }
            case SORT_BY_TITLE_DESC: {
                result.sort(new Document.SortByTitle().reversed());
                break;
            }
            case SORT_BY_RATE_ASC: {
                result.sort(new Document.SortByRate());
                break;
            }
            case SORT_BY_RATE_DESC: {
                result.sort(new Document.SortByRate().reversed());
                break;
            }
            case SORT_BY_DATE_ASC: {
                result.sort(new Document.SortByDate());
                break;
            }
            case SORT_BY_DATE_DESC: {
                result.sort(new Document.SortByDate().reversed());
                break;
            }
            case SORT_BY_BORROWED_ASC: {
                result.sort(new Document.SortByBorrowed());
                break;
            }
            case SORT_BY_BORROWED_DESC: {
                result.sort(new Document.SortByBorrowed().reversed());
                break;
            }
        }
        documentsContainer.getChildren().clear();
        documentsContainer.getChildren().add(new ListDocumentsComponent(result, scrollPane, this).getContainer());
    }
}
