package org.example.librarymanager.components;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import org.example.librarymanager.Common;

public class Avatar implements Component {
    private ImageView avatar;
    private int size;

    public Avatar(ImageView avatar, int size, String imageLink) {
        this.avatar = avatar;
        this.size = size;
        avatar.setFitWidth(size);
        avatar.setFitHeight(size);
        Task<Image> imgTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                Image image = new Image(imageLink);
                if (image != null) {
                    return image;
                }
                return Common.DEFAULT_PROFILE;
            }
        };
        imgTask.setOnSucceeded(e -> {
            avatar.setImage(imgTask.getValue());
        });
        new Thread(imgTask).start();
        Circle circle = new Circle(size / 2);
        circle.setCenterX(size / 2);
        circle.setCenterY(size / 2);
        avatar.setClip(circle);
    }

    @Override
    public Node getElement() {
        return avatar;
    }
}
