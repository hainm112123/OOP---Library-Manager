package org.example.librarymanager.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;

public class DialogComponent {
    public static final String DIALOG_WARNING = "warning";
    public static final String DIALOG_INFO = "info";

    private MFXGenericDialog dialogContent;
    private MFXStageDialog dialog;

    public DialogComponent(String title, String content, String style, Stage stage, Pane container) {
        dialogContent = MFXGenericDialogBuilder.build()
                .setContentText(content)
                .makeScrollable(true)
                .get();
        dialog = MFXGenericDialogBuilder.build(dialogContent)
                .toStageDialogBuilder()
                .initOwner(stage)
                .initModality(Modality.APPLICATION_MODAL)
                .setDraggable(false)
                .setTitle(title)
                .setOwnerNode(container)
                .setScrimPriority(ScrimPriority.WINDOW)
                .setScrimOwner(true)
                .get();
        if (style.equals(DIALOG_INFO)) {
            MFXFontIcon infoIcon = new MFXFontIcon("fas-circle-info", 18);
            dialogContent.setHeaderIcon(infoIcon);
            dialogContent.getStyleClass().add("mfx-info-dialog");
        } else if (style.equals(DIALOG_WARNING)) {
            MFXFontIcon infoIcon = new MFXFontIcon("fas-circle-exclamation", 18);
            dialogContent.setHeaderIcon(infoIcon);
            dialogContent.getStyleClass().add("mfx-warn-dialog");
        }
        dialogContent.getStylesheets().add(getClass().getResource("/org/example/librarymanager/css/MFXDialogs.css").toExternalForm());
    }

    public void addConfirmAction(EventHandler<MouseEvent> action) {
        if (action != null) {
            dialogContent.addActions(Map.entry(new MFXButton("Confirm"), action));
        } else {
            dialogContent.addActions(Map.entry(new MFXButton("Confirm"), e -> dialog.close()));
        }
    }

    public void addCancelAction(EventHandler<MouseEvent> action) {
        if (action != null) {
            dialogContent.addActions(Map.entry(new MFXButton("Cancel"), action));
        } else {
            dialogContent.addActions(Map.entry(new MFXButton("Cancel"), e -> dialog.close()));
        }
    }

    public void show() {
        dialog.showDialog();
    }

    public void close() {
        dialog.close();
    }
}
