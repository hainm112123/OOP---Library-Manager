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

    /**
     * create a circled image view for avatar
     * @param avatar
     * @param size
     * @param imageLink
     */
    public Avatar(ImageView avatar, int size, String imageLink) {
        this.avatar = avatar;
        this.size = size;
        avatar.setFitWidth(size);
        avatar.setFitHeight(size);
        Task<Image> imgTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                if (imageLink != null && !imageLink.isEmpty()) {
                    return new Image(imageLink);
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
