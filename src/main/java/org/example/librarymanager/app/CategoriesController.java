package org.example.librarymanager.app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.example.librarymanager.components.DocumentComponent;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.data.DocumentQuery;

import static org.example.librarymanager.data.DocumentQuery.*;

public class CategoriesController extends ControllerWrapper {
    @FXML
    private Label currentCategoryLabel;
    @FXML
    AnchorPane currentCategoryPane;

    private void display(List<Document> documents, AnchorPane container) {
        for (int i = 0; i < documents.size(); ++ i) {
            Document document = documents.get(i);
            VBox doc = new DocumentComponent(document).getElement();
            AnchorPane.setLeftAnchor(doc, (double)(i * (DocumentComponent.DOC_COMPONENT_WITDH + DocumentComponent.DOC_COMPONENT_OFFSET)));
            container.getChildren().add(doc);
        }
        container.setPrefWidth(documents.size() * (DocumentComponent.DOC_COMPONENT_WITDH + DocumentComponent.DOC_COMPONENT_OFFSET));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentCategoryLabel.setText(ControllerWrapper.getCurrentCategory());
        List<Document> documentList = getDocumentsByCategory(ControllerWrapper.getCurrentCategory(), 10);
        for (Document document : documentList) {
            System.out.println(document);
        }
        display(documentList, currentCategoryPane);
    }




}
