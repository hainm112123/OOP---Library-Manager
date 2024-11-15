package org.example.librarymanager.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.User;

import java.net.URL;
import java.util.List;
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
            enableDelete();
//            System.out.println(gridPane.getChildren().size());
            Node permissionNode = getGridPane(7, 1);
            ComboBox<Common.Choice> comboBox = new ComboBox<>();
//            comboBox.getItems().addAll(User.USER_TYPE_STRING);
//            comboBox.setValue(User.USER_TYPE_STRING[data.getPermission()]);
            comboBox.getItems().addAll(
                    new Common.Choice(User.TYPE_USER, User.USER_TYPE_STRING[User.TYPE_USER]),
                    new Common.Choice(User.TYPE_MODERATOR, User.USER_TYPE_STRING[User.TYPE_MODERATOR]),
                    new Common.Choice(User.TYPE_ADMIN, User.USER_TYPE_STRING[User.TYPE_ADMIN])
            );
            comboBox.setValue(new Common.Choice(data.getPermission(), User.USER_TYPE_STRING[data.getPermission()]));

//            comboBox.getItems().remove(User.USER_TYPE_STRING[data.getPermission()]);
            replaceGridPane(7, 1, comboBox);
            GridPane.setHgrow(comboBox, Priority.ALWAYS);
            comboBox.setMaxWidth(Double.MAX_VALUE);
        });
    }

    @Override
    protected void enableApply() {
        applyBtn.setOnAction(e -> {
            applyQuery();
        });
    }

//    @Override
//    protected void applyQuery() {
//        int id = Integer.parseInt(((TextField)gridPane.getChildren().get(1)).getText());
//        ComboBox text = (ComboBox)gridPane.getChildren().get(15);
//        String value = (String)text.getValue();
//        int t = 3;
//        for (int i = 0; i < 3; i++) if(value == User.USER_TYPE_STRING[i]) {
//            t = i;
//            break;
//        }
//        if (UserQuery.getInstance().updatePermissionById(id, t)) {
//            message.setText("Successfully applied!");
//        }
//    }
}
