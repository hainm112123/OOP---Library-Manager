package org.example.librarymanager.components;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.librarymanager.Common;
import org.example.librarymanager.app.ControllerWrapper;
import org.example.librarymanager.models.Document;

public class DocumentComponent implements Component {
    private Node container;

    private AnchorPane gridContainer;
    private VBox gridBox;
    private ImageView imageView;
    private Label title;

    private HBox listContainer;
    private VBox detailBox;
    private HBox dataBox;
    private Label rating;
    private Label borrowed;
    private Label category;
    private Label description;

    private ControllerWrapper controller;

    public static final int DOC_COMPONENT_WITDH_GRID = 144;
    public static final int DOC_COMPONENT_HEIGHT_GRID = 300;
    public static final int DOC_COMPONENT_WIDTH_LIST = 500;
    public static final int DOC_COMPONENT_HEIGHT_LIST = 232;
    public static final int DOC_COMPONENT_OFFSET = 30;
    public static final int VIEW_TYPE_GRID = 0;
    public static final int VIEW_TYPE_LIST = 1;

    private static final int IMAGE_WIDTH = 144;
    private static final int IMAGE_HEIGHT = 216;
    private static final DropShadow ds = new DropShadow();
    private static final DropShadow ds_hover = new DropShadow();

    static {
        ds.setRadius(15);
        ds_hover.setRadius(30);
    }

    /**
     * component to display in grid-view
     * @param document
     */
    public void initGridView(Document document) {
        gridBox = new VBox();
        gridContainer = new AnchorPane();

        gridBox.getChildren().add(imageView);
        title = new Label(document.getTitle());
        title.getStyleClass().add("document-title");
        title.setMinWidth(DOC_COMPONENT_WITDH_GRID);
        title.setPrefWidth(DOC_COMPONENT_WITDH_GRID);
        title.setMaxWidth(DOC_COMPONENT_WITDH_GRID);
        title.setEllipsisString("...");
        gridBox.getChildren().add(title);
        gridBox.setAlignment(Pos.CENTER);
        gridContainer.getChildren().add(gridBox);
        gridContainer.getStylesheets().add(getClass().getResource("/org/example/librarymanager/css/document.css").toExternalForm());

        gridContainer.setOnMouseClicked((event) -> {
            ControllerWrapper.setCurrentDocument(document);
            controller.safeSwitchScene("document-detail.fxml");
        });

        TranslateTransition up = new TranslateTransition(Duration.millis(200), imageView);
        up.setToY(-10);
        TranslateTransition down = new TranslateTransition(Duration.millis(200), imageView);
        down.setToY(0);
        imageView.setEffect(ds);
        gridContainer.setOnMouseEntered(e -> {
            imageView.setEffect(ds_hover);
            up.playFromStart();
        });
        gridContainer.setOnMouseExited(e -> {
            imageView.setEffect(ds);
            down.playFromStart();
        });
        gridContainer.getStyleClass().add("document-container");
    }

    /**
     * component to display in list-view
     * @param document
     */
    public void initListView(Document document) {
        listContainer = new HBox();
        listContainer.setAlignment(Pos.CENTER);
        detailBox = new VBox();
        title = new Label(document.getTitle());
        title.setEllipsisString("...");
        dataBox = new HBox();
        rating = new Label(String.format("%.2f",document.getRating()), new FontAwesomeIconView(FontAwesomeIcon.STAR));
        borrowed = new Label("" + document.getBorrowedTimes(), new FontAwesomeIconView(FontAwesomeIcon.BOOKMARK));
        category = new Label(document.getCategoryName());
        description = new Label(document.getDescription());
        description.setWrapText(true);
        description.setEllipsisString("...");

        dataBox.getChildren().addAll(rating, borrowed);
        detailBox.getChildren().addAll(title, dataBox, category, description);
        listContainer.getChildren().addAll(imageView, detailBox);
        listContainer.getStylesheets().add(getClass().getResource("/org/example/librarymanager/css/document.css").toExternalForm());
        title.getStyleClass().addAll("document-title", "document-title-list");
        dataBox.getStyleClass().add("document-data-box");
        rating.getStyleClass().add("document-data");
        borrowed.getStyleClass().add("document-data");
        category.getStyleClass().add("document-category");
        description.getStyleClass().add("document-description");
        detailBox.getStyleClass().add("document-detail-box");
        listContainer.getStyleClass().add("document-list-container");

        listContainer.setOnMouseClicked((event) -> {
            ControllerWrapper.setCurrentDocument(document);
            controller.safeSwitchScene("document-detail.fxml");
        });
        listContainer.setEffect(ds);
    }

    /**
     * Construct a component of document details.
     * Image is loaded in a task which is executed in a new distinct thread.
     * @param document document to display
     * @param controller current controller
     */
    public DocumentComponent(Document document, ControllerWrapper controller, int type) {
        this.controller = controller;
        imageView = new ImageView(Common.NO_IMAGE);
        if (document.getImageLink() != null) {
            Task<Image> task = new Task<Image>() {
                @Override
                protected Image call() {
                    try {
                        return new Image(document.getImageLink(), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
            task.setOnSucceeded((event) -> {
                if (task.getValue() != null) {
                    imageView.setImage(task.getValue());
                }
            });
            new Thread(task).start();
        }
        imageView.setFitWidth(IMAGE_WIDTH);
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.getStyleClass().add("document-image");

        if (type == VIEW_TYPE_GRID) {
            initGridView(document);
            container = gridContainer;
        } else {
            initListView(document);
            container = listContainer;
        }
    }

    /**
     * Return container.
     */
    @Override
    public Node getElement() {
        return container;
    }
}
