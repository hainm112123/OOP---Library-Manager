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
import javafx.scene.layout.VBox;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.PendingService;
import org.example.librarymanager.models.User;

public class BorrowRequestComponent implements Component {
    public static final int COMPONENT_HEIGHT = 64;

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

    public BorrowRequestComponent(PendingService service) {
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
        Common.hide(progressBar);

        approveButton.setOnAction(e -> onButtonClicked(service, true));
        declineButton.setOnAction(e -> onButtonClicked(service, false));
    }

    private void onButtonClicked(PendingService service, boolean isApproved) {
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
                if (container.getParent() instanceof VBox) {
                    ((VBox) container.getParent()).getChildren().remove(container);
                }
            }
        });
        new Thread(task).start();
    }

    @Override
    public Node getElement() {
        return container;
    }
}
