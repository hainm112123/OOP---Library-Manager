package org.example.librarymanager.components;

import javafx.scene.control.Pagination;
import org.example.librarymanager.models.Document;

import java.util.List;

public class ListDocumentsObserver implements Observer {
    private List<Document> documents;
    private ListDocumentsComponent component;

    public ListDocumentsObserver(List<Document> documents, ListDocumentsComponent component) {
        this.documents = documents;
        this.component = component;
    }

    @Override
    public void update(Subject subject) {
        component.setDocumentsGridPane(documents, component.getPagination().getCurrentPageIndex());
    }
}
