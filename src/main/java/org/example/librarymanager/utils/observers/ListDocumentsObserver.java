package org.example.librarymanager.utils.observers;

import org.example.librarymanager.components.ListDocumentsComponent;
import org.example.librarymanager.models.Document;

import java.util.List;

public class ListDocumentsObserver extends Observer {
    private List<Document> documents;
    private ListDocumentsComponent component;

    /**
     * observer for list documents component, listen to the change documents' display type event
     * @param documents
     * @param component
     */
    public ListDocumentsObserver(List<Document> documents, ListDocumentsComponent component) {
        this.documents = documents;
        this.component = component;
    }

    @Override
    public void update(Subject subject) {
        component.setDocumentsGridPane(documents, component.getPagination().getCurrentPageIndex());
    }
}
