package org.example.librarymanager.components;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListDocumentsSubject implements Subject {
    private int displayType = DocumentComponent.VIEW_TYPE_GRID;
    private Label gridViewBtn;
    private Label listViewBtn;
    private FontAwesomeIconView gridIcon;
    private FontAwesomeIconView listIcon;
    private List<Observer> observers = new ArrayList<>();

    public ListDocumentsSubject(Label gridViewBtn, Label listViewBtn, FontAwesomeIconView gridIcon, FontAwesomeIconView listIcon) {
        this.gridViewBtn = gridViewBtn;
        this.listViewBtn = listViewBtn;
        this.gridIcon = gridIcon;
        this.listIcon = listIcon;
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    public void changeDisplayType(int displayType) {
        if (this.displayType != displayType) {
            this.displayType = displayType;
            if (displayType == DocumentComponent.VIEW_TYPE_GRID) {
                gridIcon.setFill(Paint.valueOf("#fff"));
                gridViewBtn.getStyleClass().add("view-type-btn--active");
                listIcon.setFill(Paint.valueOf("#000"));
                listViewBtn.getStyleClass().remove("view-type-btn--active");
            }
            else {
                listIcon.setFill(Paint.valueOf("#fff"));
                listViewBtn.getStyleClass().add("view-type-btn--active");
                gridIcon.setFill(Paint.valueOf("#000"));
                gridViewBtn.getStyleClass().remove("view-type-btn--active");
            }
            notifyObservers();
        }
    }
}
