package org.example.librarymanager.app;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.DocumentComponent;
import org.example.librarymanager.data.DocumentQuery;
import org.example.librarymanager.data.RatingQuery;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.RecommendationData;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class HomeController extends ControllerWrapper{
    @FXML
    private AnchorPane mostPopularContainer;
    @FXML
    private AnchorPane highestRatedContainer;
    @FXML
    private AnchorPane newsContainer;
    @FXML
    private AnchorPane recommendationsContainer;

    private static final int NUM_DOCUMENT_DISPLAY = 15;
    private static List<Integer> recommendations;

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
     * Get recommendation documents
     */
    private void getRecommendations() {
        if (recommendations != null) return;
        List<RecommendationData> dataModel = RatingQuery.getInstance().getDataModel();
        Map<Integer, HashMap<Integer, Double>> rate = new HashMap<>();
        Map<Integer, HashMap<Integer, Double>> diff = new HashMap<>();
        Map<Integer, HashMap<Integer, Integer>> count = new HashMap<>();
        for (RecommendationData data: dataModel) {
            if (!rate.containsKey(data.getUserId())) {
                rate.put(data.getUserId(), new HashMap<>());
            }
            rate.get(data.getUserId()).put(data.getDocumentId(), data.getPreference());
        }
        for (HashMap<Integer, Double> items: rate.values()) {
            for (Integer item1: items.keySet()) {
                if (!diff.containsKey(item1)) {
                    diff.put(item1, new HashMap<>());
                    count.put(item1, new HashMap<>());
                }
                for (Integer item2: items.keySet()) if (Integer.compare(item1, item2) != 0) {
                    diff.get(item1).put(item2, diff.get(item1).getOrDefault(item2, 0.0) + items.get(item1) - items.get(item2));
                    count.get(item1).put(item2, count.get(item1).getOrDefault(item2, 0) + 1);
                }
            }
        }
        if (rate.isEmpty()) {
            return;
        }
        HashMap<Integer, Double> preference;
        if (!rate.containsKey(getUser().getId())) {
            List<Integer> keys = rate.keySet().stream().toList();
            Random random = new Random();
            preference = rate.get(keys.get(random.nextInt(keys.size())));
        } else {
            preference = rate.get(getUser().getId());
        }
        List<Common.Item<Integer, Double>> predicts = new ArrayList<>();
        for (Integer item1: diff.keySet()) {
            if (preference.containsKey(item1)) {
                continue;
            }
            double sum = 0;
            int total = 0;
            for (Integer item2: preference.keySet()) {
                sum += diff.get(item1).getOrDefault(item2, 0.0) + preference.get(item2) * count.get(item1).getOrDefault(item2, 0);
                total += count.get(item1).getOrDefault(item2, 0);
            }
            predicts.add(new Common.Item<>(item1, sum / total));
        }
        Collections.sort(predicts, new Common.Item.compareByValue<Integer, Double>().reversed());
        recommendations = predicts.stream().map(Common.Item::getKey).toList();
    }

    /**
     * Initialization.
     * Executor manages 2 threads: 1 for popular docs, 1 for high rated docs.
     * @param location url to home.fxml
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newFixedThreadPool(4);
        Future<List<Document>> mostPoularDocFu = executor.submit(() -> DocumentQuery.getInstance().getMostPopularDocuments(NUM_DOCUMENT_DISPLAY));
        Future<List<Document>> highestRateDocFu = executor.submit(() -> DocumentQuery.getInstance().getHighestRatedDocuments(NUM_DOCUMENT_DISPLAY));
        Future<List<Document>> recDocFu = executor.submit(() -> {
            getRecommendations();
            return DocumentQuery.getInstance().getDocumentsFromIds(recommendations);
        });
        Future<List<Document>> newDocFu = executor.submit(() -> DocumentQuery.getInstance().getNewestDocuments(NUM_DOCUMENT_DISPLAY));
        executor.shutdown();
        try {
            List<Document> mostPopularDocuments = mostPoularDocFu.get();
            List<Document> highestRatedDocuments = highestRateDocFu.get();
            List<Document> recommendedDocuments = recDocFu.get();
            List<Document> newDocuments = newDocFu.get();
//            if (recommendedDocuments.isEmpty()) recommendedDocuments = highestRatedDocuments;
            display(newDocuments, newsContainer);
            display(recommendedDocuments, recommendationsContainer);
            display(mostPopularDocuments, mostPopularContainer);
            display(highestRatedDocuments, highestRatedContainer);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
