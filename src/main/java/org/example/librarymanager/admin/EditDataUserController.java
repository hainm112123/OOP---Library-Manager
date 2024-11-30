package org.example.librarymanager.admin;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.User;

import java.net.URL;
import java.util.ResourceBundle;

public class EditDataUserController extends EditDataController<User> {
//    @FXML
//    GridPane gridPane;
//    @FXML
//    Label title;
//    @FXML
//    Button backBtn;
//    @FXML
//    Button applyBtn;
//    @FXML
//    Button deleteBtn;
//    @FXML
//    Label message;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        dataAccessObject = UserQuery.getInstance();
        Platform.runLater(() -> {
            enableApply();
            if (data.getPermission() != User.TYPE_ADMIN) {
                enableDelete();
            }

            Node permissionNode = getGridPane(8, 1);
            MFXComboBox<Common.Choice> comboBox = new MFXComboBox<>();

            comboBox.setFloatMode(FloatMode.DISABLED);
            if (data.getPermission() == User.TYPE_ADMIN) {
                comboBox.getItems().add(new Common.Choice(User.TYPE_ADMIN, User.USER_TYPE_STRING[User.TYPE_ADMIN]));
                comboBox.getSelectionModel().selectFirst();
                comboBox.setDisable(true);
            } else {
                comboBox.getItems().addAll(
                        new Common.Choice(User.TYPE_USER, User.USER_TYPE_STRING[User.TYPE_USER]),
                        new Common.Choice(User.TYPE_MODERATOR, User.USER_TYPE_STRING[User.TYPE_MODERATOR])//,
                );
                if (data.getPermission() == User.TYPE_USER) {
                    comboBox.getSelectionModel().selectFirst();
                } else {
                    comboBox.getSelectionModel().selectLast();
                }
            }

            replaceGridPane(8, 1, comboBox);
            GridPane.setHgrow(comboBox, Priority.ALWAYS);
            comboBox.setMaxWidth(Double.MAX_VALUE);
        });
    }

}
