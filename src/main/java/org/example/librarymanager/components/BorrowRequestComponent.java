package org.example.librarymanager.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.librarymanager.Common;
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.data.NotificationQuery;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Notification;
import org.example.librarymanager.models.PendingService;
import org.example.librarymanager.models.User;

public class BorrowRequestComponent implements Component {
    public static final int COMPONENT_HEIGHT = 64 + 24;

    private VBox wrapper;
    private HBox container;
    private Label username;
    private ImageView userAvatar;
    private ImageView documentImage;
    private VBox documentBox;
    private Label documentTitle;
    private Label documentCategory;
    private VBox buttonGroup;
    private MFXButton approveButton;
    private MFXButton declineButton;
    private MFXProgressBar progressBar;

    public BorrowRequestComponent(PendingService service, Pane root) {
        wrapper = new VBox();
        container = new HBox();
        username = new Label(service.getUsername());
        userAvatar = (ImageView) new Avatar(new ImageView(), 54, service.getUserAvatar()).getElement();
        documentImage = Common.loadImage(new ImageView(), 36, 54, service.getDocumentImage());
        documentBox = new VBox();
        documentTitle = new Label(service.getDocumentTitle());
        documentCategory = new Label(service.getDocumentCategory());
        buttonGroup = new VBox();
        progressBar = new MFXProgressBar();
        approveButton = new MFXButton("Approve");
        declineButton = new MFXButton("Decline");
        approveButton.setCursor(Cursor.HAND);
        declineButton.setCursor(Cursor.HAND);
        Separator separtor1 = new Separator(Orientation.VERTICAL);
        Separator separtor2 = new Separator(Orientation.VERTICAL);
        separtor1.setPrefWidth(40);
        separtor2.setPrefWidth(40);
        StackPane gap = new StackPane();
        gap.setPrefHeight(24);
        gap.setStyle("-fx-background-color: #fff");
        container.getChildren().add(gap);

        container.getStylesheets().add(getClass().getResource("/org/example/librarymanager/css/request.css").toExternalForm());
        container.getStyleClass().add("request-container");
        username.getStyleClass().add("request-username");
        documentBox.getStyleClass().add("request-document-box");
        documentTitle.getStyleClass().add("request-document-title");
        documentCategory.getStyleClass().add("request-document-category");
        buttonGroup.getStyleClass().add("request-button-group");
        approveButton.getStyleClass().add("request-approve-button");
        declineButton.getStyleClass().add("request-decline-button");

        documentBox.getChildren().addAll(documentTitle, documentCategory);
        buttonGroup.getChildren().addAll(approveButton, progressBar, declineButton);
        container.getChildren().addAll(userAvatar, username, separtor1, documentImage, documentBox, separtor2, buttonGroup);
        wrapper.getChildren().addAll(container, gap);

        Common.hide(progressBar);
        approveButton.setOnAction(e -> onButtonClicked(service, true, root));
        declineButton.setOnAction(e -> onButtonClicked(service, false, root));
    }

    private void onButtonClicked(PendingService service, boolean isApproved, Pane root) {
        Common.hide(approveButton);
        Common.hide(declineButton);
        Common.show(progressBar);
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return ServiceQuery.getInstance().executeBorrowRequest(service.getUserId(), service.getDocumentId(), isApproved);
            }
        };
        task.setOnSucceeded(e -> {
            Common.show(approveButton);
            Common.show(declineButton);
            Common.hide(progressBar);
            if (task.getValue()) {
                if (wrapper.getParent() instanceof VBox) {
                    VBox parent = (VBox) wrapper.getParent();
                    parent.getChildren().remove(wrapper);
                    parent.setPrefHeight(parent.getChildren().size() * BorrowRequestComponent.COMPONENT_HEIGHT);
                }
                Task<Void> updateNotiTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Notification notification = new Notification(service.getUserId(), service.getDocumentId(), isApproved ? Notification.TYPE.REQUEST_APPROVED.ordinal() : Notification.TYPE.REQUEST_DECLINED.ordinal());
                        NotificationQuery.getInstance().add(notification);
                        return null;
                    }
                };
                new Thread(updateNotiTask).start();
            } else {
                DialogComponent dialog = new DialogComponent(
                        isApproved ? "Approve" : "Decline",
                        isApproved ? "There is no book remain." : "Some errors occurred! Please try again.",
                        DialogComponent.DIALOG_WARNING,
                        ControllerWrapper.getStage(),
                        root
                );
                dialog.addConfirmAction(event -> dialog.close());
                dialog.show();
            }
        });
        new Thread(task).start();
    }

    @Override
    public Node getElement() {
        return wrapper;
    }
}
