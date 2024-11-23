package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.example.librarymanager.Common;
import org.example.librarymanager.components.Avatar;
import org.example.librarymanager.data.Backblaze;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.User;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProfileController extends ControllerWrapper {
    @FXML
    private Label FirstName;
    @FXML
    private Label LastName;
    @FXML
    private Label Gender;
    @FXML
    private Label DateOfBirth;
    @FXML
    private MFXButton ChangeName;
    @FXML
    private MFXButton SaveName;
    @FXML
    private MFXButton Cancel;
    @FXML
    private MFXTextField FName;
    @FXML
    private MFXTextField LName;
    @FXML
    private MFXComboBox<String> GenderField;
    @FXML
    private MFXDatePicker DateOfBirthField;
    @FXML
    private AreaChart<String,Number> StatisticalTimeUse;
    @FXML
    private Label UserName;
    @FXML
    private ImageView profileImage;
    @FXML
    private Pane profileImgOverlay;
    @FXML
    private MFXButton saveImageBtn;
    @FXML
    private MFXProgressSpinner loader;

    private File imageFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GenderField.getItems().addAll("Male", "Female");
        Common.disable(GenderField);
        Common.disable(DateOfBirthField);
        resetName();
        UserName.setText((String)("@" + User.USER_TYPE_STRING[getUser().getPermission()] + " " + getUser().getUsername()));

        Cancel.setRippleColor(Paint.valueOf(Common.PRIMARY_COLOR));
        SaveName.setRippleColor(Paint.valueOf(Common.PRIMARY_COLOR));
        ChangeName.setRippleColor(Paint.valueOf(Common.PRIMARY_COLOR));

        ChangeName.setOnAction(event -> {
            ChangeStatus();
        });

        Cancel.setOnAction(event -> {
            resetName();
            NotChangeStatus();
        });

        SaveName.setOnAction(event -> {
            getUser().setFirstname(FName.getText());
            getUser().setLastname(LName.getText());
            getUser().setGender(GenderField.getValue());
            if (DateOfBirthField.getValue() != null) {
                getUser().setDateOfBirth(DateOfBirthField.getValue());
            }
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return UserQuery.getInstance().update(getUser());
                }
            };
            task.setOnSucceeded(e -> {
                resetName();
                NotChangeStatus();
            });
            new Thread(task).start();
        });

        profileImage = (ImageView) new Avatar(profileImage, 200, getUser().getImageLink()).getElement();
        Common.disable(saveImageBtn);
        Common.disable(loader);
        profileImgOverlay.setOpacity(0.0);
        profileImage.setCursor(Cursor.HAND);
        Circle circle = new Circle(100);
        circle.setCenterX(100);
        circle.setCenterY(100);
        profileImage.setClip(circle);
        profileImgOverlay.setOnMouseEntered(e -> {
            profileImgOverlay.setOpacity(1.0);
        });
        profileImgOverlay.setOnMouseExited(e -> {
            profileImgOverlay.setOpacity(0.0);
        });

        profileImgOverlay.setOnMouseClicked(e -> {
           FileChooser fileChooser = new FileChooser();
           fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
           fileChooser.setTitle("Select Profile Image");
           imageFile = fileChooser.showOpenDialog(stage);
           if (imageFile != null) {
               Common.enable(saveImageBtn);
               Image image = new Image(imageFile.toURI().toString());
               profileImage.setImage(image);
           } else {
               Common.disable(saveImageBtn);
           }
        });
        saveImageBtn.setOnAction(e -> {
            Common.enable(loader);
            Common.disable(saveImageBtn);
            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    String url = Backblaze.getInstance().upload(String.format("user-profile-%d.png", getUser().getId()), imageFile.getAbsolutePath());
                    getUser().setImageLink(url);
                    return UserQuery.getInstance().update(getUser());
                }
            };
            task.setOnSucceeded(event -> {
                Common.disable(loader);
            });
            new Thread(task).start();
        });
    }

    public void resetName(){
        FirstName.setText(getUser().getFirstname());
        LastName.setText(getUser().getLastname());
        Gender.setText(getUser().getGender());
        if (getUser().getDateOfBirth() != null) {
            DateOfBirth.setText(getUser().getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            DateOfBirth.setText("n/a");
        }
        FName.setText(getUser().getFirstname());
        LName.setText(getUser().getLastname());
        if (getUser().getGender().equals("Male")) {
            GenderField.getSelectionModel().selectFirst();
        } else {
            GenderField.getSelectionModel().selectLast();
        }
    }

    public void ChangeStatus(){
        FName.setVisible(true);
        LName.setVisible(true);
        FirstName.setVisible(false);
        LastName.setVisible(false);
        Cancel.setVisible(true);
        SaveName.setVisible(true);
        ChangeName.setVisible(false);
        Common.enable(GenderField);
        Common.disable(Gender);
        Common.enable(DateOfBirthField);
        Common.disable(DateOfBirth);
    }
    public void NotChangeStatus(){
        FName.setVisible(false);
        LName.setVisible(false);
        FirstName.setVisible(true);
        LastName.setVisible(true);
        SaveName.setVisible(false);
        Cancel.setVisible(false);
        ChangeName.setVisible(true);
        Common.disable(GenderField);
        Common.enable(Gender);
        Common.disable(DateOfBirthField);
        Common.enable(DateOfBirth);
    }

}
