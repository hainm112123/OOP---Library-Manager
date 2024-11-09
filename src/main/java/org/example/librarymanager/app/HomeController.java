package org.example.librarymanager.app;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.example.librarymanager.components.DocumentComponent;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.models.Document;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class HomeController extends ControllerWrapper{
    @FXML
    private AnchorPane mostPopularContainer;
    @FXML
    private AnchorPane highestRatedContainer;

    /**
     * Display documents as document components on a scroll pane.
     * Each document component can set current document and switch scene to document detail.
     * @param documents the list of documents to display
     * @param container the pane in which the documents are displayed
     */
    private void display(List<Document> documents, AnchorPane container) {
        container.setPrefWidth(documents.size() * (DocumentComponent.DOC_COMPONENT_WITDH_GRID + DocumentComponent.DOC_COMPONENT_OFFSET) + 48);
        for (int i = 0; i < documents.size(); ++ i) {
            Document document = documents.get(i);
            Node doc = new DocumentComponent(document, this, DocumentComponent.VIEW_TYPE_GRID).getElement();
            AnchorPane.setTopAnchor(doc, 30.0);
            AnchorPane.setLeftAnchor(doc, (double)(i * (DocumentComponent.DOC_COMPONENT_WITDH_GRID + DocumentComponent.DOC_COMPONENT_OFFSET)) + 24);
            container.getChildren().add(doc);
        }
    }

    /**
     * Initialization.
     * Executor manages 2 threads: 1 for popular docs, 1 for high rated docs.
     * @param location url to home.fxml
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newFixedThreadPool(2);
        Future<List<Document>> mostPoularDocFu = executor.submit(() -> DocumentQuery.getInstance().getMostPopularDocuments(15));
        Future<List<Document>> highestRateDocFu = executor.submit(() -> DocumentQuery.getInstance().getHighestRatedDocuments(15));
        executor.shutdown();
        try {
            List<Document> mostPopularDocuments = mostPoularDocFu.get();
            List<Document> highestRatedDocuments = highestRateDocFu.get();
//            executor.submit(() -> display(mostPopularDocuments, mostPopularContainer));
//            executor.submit(() -> display(highestRatedDocuments, highestRatedContainer));
            display(mostPopularDocuments, mostPopularContainer);
            display(highestRatedDocuments, highestRatedContainer);
//            executor.shutdown();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
//            executor.shutdown();
        }
    }
}
