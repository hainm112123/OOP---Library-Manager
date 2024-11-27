package org.example.librarymanager.utils.observers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import lombok.Data;
import org.example.librarymanager.components.DocumentComponent;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListDocumentsSubject extends Subject {
    private int displayType = DocumentComponent.VIEW_TYPE_GRID;
    private Label gridViewBtn;
    private Label listViewBtn;
    private FontAwesomeIconView gridIcon;
    private FontAwesomeIconView listIcon;

    /**
     * list documents' subject: a button-group that can change view-type between grid-view and list-view
     * @param gridViewBtn
     * @param listViewBtn
     * @param gridIcon
     * @param listIcon
     */
    public ListDocumentsSubject(Label gridViewBtn, Label listViewBtn, FontAwesomeIconView gridIcon, FontAwesomeIconView listIcon) {
        this.gridViewBtn = gridViewBtn;
        this.listViewBtn = listViewBtn;
        this.gridIcon = gridIcon;
        this.listIcon = listIcon;
        gridViewBtn.setOnMouseClicked(e -> changeDisplayType(DocumentComponent.VIEW_TYPE_GRID));
        listViewBtn.setOnMouseClicked(e -> changeDisplayType(DocumentComponent.VIEW_TYPE_LIST));
    }

    /**
     * change view type
     * @param displayType
     */
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
