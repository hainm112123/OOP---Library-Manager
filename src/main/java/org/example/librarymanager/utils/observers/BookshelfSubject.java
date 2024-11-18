package org.example.librarymanager.utils.observers;

import javafx.scene.control.Label;
import lombok.Data;
import org.example.librarymanager.models.Service;

import java.util.ArrayList;
import java.util.List;

public class BookshelfSubject extends Subject {
    private int status;
    private Label readingBtn;
    private Label wishlistBtn;
    private Label completedBtn;

    public BookshelfSubject(Label readingBtn, Label wishlistBtn, Label completedBtn) {
        this.readingBtn = readingBtn;
        this.wishlistBtn = wishlistBtn;
        this.completedBtn = completedBtn;
        readingBtn.setOnMouseClicked(event -> changeStatus(Service.STATUS_READING));
        wishlistBtn.setOnMouseClicked(event -> changeStatus(Service.STATUS_WISH_LIST));
        completedBtn.setOnMouseClicked(event -> changeStatus(Service.STATUS_COMPLETED));
    }

    /**
     * change bookshelf status
     * @param status
     */
    public void changeStatus(int status) {
        if (status == this.status) return;
        this.status = status;
        if (status == Service.STATUS_READING) {
            readingBtn.getStyleClass().add("status-button--active");
            wishlistBtn.getStyleClass().remove("status-button--active");
            completedBtn.getStyleClass().remove("status-button--active");
        } else if (status == Service.STATUS_COMPLETED) {
            readingBtn.getStyleClass().remove("status-button--active");
            wishlistBtn.getStyleClass().remove("status-button--active");
            completedBtn.getStyleClass().add("status-button--active");
        } else {
            readingBtn.getStyleClass().remove("status-button--active");
            wishlistBtn.getStyleClass().add("status-button--active");
            completedBtn.getStyleClass().remove("status-button--active");
        }
        notifyObservers();
    }

    public int getStatus() {
        return status;
    }
}
