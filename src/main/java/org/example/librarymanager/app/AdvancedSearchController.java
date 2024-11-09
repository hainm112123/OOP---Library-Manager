package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.ListDocumentsComponent;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    private Directory memoryIndex;
    private Analyzer analyzer;

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

        executor = Executors.newFixedThreadPool(3);
        Future<List<Category>> catFu = executor.submit(() -> CategoryQuery.getInstance().getAll());
        Future<List<Document>> docFu = executor.submit(() -> DocumentQuery.getInstance().getAll());
        try {
            categories = catFu.get();
            documents = docFu.get();
            executor.submit(this::initSearch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        categoryFilter.getItems().add(new Common.Choice(FILTER_CATEGORY_ANY, "Any"));
        for (Category category : categories) {
            categoryFilter.getItems().add(new Common.Choice(category.getId(), category.getName()));
        }

        sortByFilter.getSelectionModel().selectFirst();
        categoryFilter.getSelectionModel().selectFirst();
        statusFilter.getSelectionModel().selectFirst();

        documentsContainer.getChildren().clear();
        documentsContainer.getChildren().add(new ListDocumentsComponent(documents, scrollPane, this).getElement());

        searchButton.setOnAction(e -> search());
    }

    private void initSearch() {
        try {
            memoryIndex = new RAMDirectory();
            analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(memoryIndex, config);
            for (Document document: documents) {
                org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();
                luceneDocument.add(new org.apache.lucene.document.TextField("id", String.valueOf(document.getId()), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("categoryId", String.valueOf(document.getCategoryId()), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("owner", String.valueOf(document.getOwner()), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("author", document.getAuthor(), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("title", document.getTitle(), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("description", document.getDescription(), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("imageLink", document.getImageLink(), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("quantity", String.valueOf(document.getQuantity()), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("quantityInStock", String.valueOf(document.getQuantityInStock()), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("borrowedTimes", String.valueOf(document.getBorrowedTimes()), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("addDate", String.valueOf(document.getAddDate()), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("rating", String.valueOf(document.getRating()), Field.Store.YES));
                luceneDocument.add(new org.apache.lucene.document.TextField("categoryName", document.getCategoryName(), Field.Store.YES));
                writer.addDocument(luceneDocument);
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void search() {
        List<Document> result = documents;
        if (!searchBox.getText().isEmpty()) {
            try {
                String[] fields = {"title", "description"};
                Query query = new MultiFieldQueryParser(fields, analyzer).parse(searchBox.getText());
                IndexReader indexReader = DirectoryReader.open(memoryIndex);
                IndexSearcher searcher = new IndexSearcher(indexReader);
                TopDocs topDocs = searcher.search(query, documents.size());
                result = new ArrayList<>();
                for (ScoreDoc doc: topDocs.scoreDocs) {
                    result.add(new Document(searcher.doc(doc.doc)));
//                    System.out.println(searcher.doc(doc.doc));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        documentsContainer.getChildren().add(new ListDocumentsComponent(result, scrollPane, this).getElement());
    }
}
