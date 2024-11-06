package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import org.example.librarymanager.Common;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController extends ControllerWrapper {
    @FXML
    private MFXTextField currentPassword;
    @FXML
    private MFXTextField newPassword;
    @FXML
    private MFXTextField confirmPassword;
    @FXML
    private MFXButton submitBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        submitBtn.setRippleColor(Paint.valueOf(Common.PRIMARY_COLOR));
    }
}
