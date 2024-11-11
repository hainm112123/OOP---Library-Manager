package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController extends ControllerWrapper {
    @FXML
    private Label FirstName;
    @FXML
    private Label LastName;
    @FXML
    private Label Gender;
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
    private AreaChart<String,Number> StatisticalTimeUse;
    @FXML
    private Label UserName;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetName();
        UserName.setText((String)("@User " + getUser().getUsername()));

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
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return UserQuery.getInstance().update(getUser());
                }
            };
            task.setOnSucceeded(e -> {
                NotChangeStatus();
            });
            new Thread(task).start();
        });
    }

    public void resetName(){
        FirstName.setText(getUser().getFirstname());
        LastName.setText(getUser().getLastname());
        Gender.setText(getUser().getGender());
        FName.setText(getUser().getFirstname());
        LName.setText(getUser().getLastname());
    }

    public void ChangeStatus(){
        FName.setVisible(true);
        LName.setVisible(true);
        FirstName.setVisible(false);
        LastName.setVisible(false);
        Cancel.setVisible(true);
        SaveName.setVisible(true);
        ChangeName.setVisible(false);
    }
    public void NotChangeStatus(){
        FName.setVisible(false);
        LName.setVisible(false);
        FirstName.setVisible(true);
        LastName.setVisible(true);
        SaveName.setVisible(false);
        Cancel.setVisible(false);
        ChangeName.setVisible(true);
    }

}
