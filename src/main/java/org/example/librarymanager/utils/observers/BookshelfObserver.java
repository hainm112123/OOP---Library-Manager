package org.example.librarymanager.utils.observers;

import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import org.example.librarymanager.components.ListDocumentsComponent;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Service;

import java.util.List;

@AllArgsConstructor
public class BookshelfObserver extends Observer {
    private VBox container;
    private ListDocumentsComponent readingComponent;
    private ListDocumentsComponent wishlistComponent;
    private ListDocumentsComponent completedComponent;

    @Override
    public void update(Subject subject) {
        if (!(subject instanceof BookshelfSubject)) return;
        BookshelfSubject bookshelfSubject = (BookshelfSubject) subject;
        container.getChildren().clear();
        if (bookshelfSubject.getStatus() == Service.STATUS_READING) {
            container.getChildren().add(readingComponent.getElement());
        } else if (bookshelfSubject.getStatus() == Service.STATUS_COMPLETED) {
            container.getChildren().add(completedComponent.getElement());
        } else {
            container.getChildren().add(wishlistComponent.getElement());
        }
    }
}
