package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.librarymanager.components.ListDocumentsComponent;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.utils.observers.BookshelfObserver;
import org.example.librarymanager.utils.observers.BookshelfSubject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BookshelfContronller extends ControllerWrapper {
    @FXML
    private MFXScrollPane scrollPane;
    @FXML
    private Label readingBtn;
    @FXML
    private Label wishlistBtn;
    @FXML
    private Label completedBtn;
    @FXML
    private VBox container;

    private ListDocumentsComponent readingComponent;
    private ListDocumentsComponent wishlistComponent;
    private ListDocumentsComponent completedComponent;

    /**
     * Display all documents user currently borrow in a grid pane.
     * Executor manages 1 thread to load documents.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newFixedThreadPool(3);
        Future<List<Document>> readingFu = executor.submit(() -> ServiceQuery.getInstance().getBorrowingDocuments(getUser().getId()));
        Future<List<Document>> wishlistFu = executor.submit(() -> ServiceQuery.getInstance().getWishlistDocuments(getUser().getId()));
        Future<List<Document>> completedFu = executor.submit(() -> ServiceQuery.getInstance().getCompletedDocument(getUser().getId()));
        executor.shutdown();
        try {
            readingComponent = new ListDocumentsComponent(readingFu.get(), scrollPane, this);
            wishlistComponent = new ListDocumentsComponent(wishlistFu.get(), scrollPane, this);
            completedComponent = new ListDocumentsComponent(completedFu.get(), scrollPane, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        container.getChildren().add(readingComponent.getElement());
        BookshelfSubject subject = new BookshelfSubject(readingBtn, wishlistBtn, completedBtn);
        BookshelfObserver observer = new BookshelfObserver(container, readingComponent, wishlistComponent, completedComponent);
        subject.attach(observer);
    }
}
