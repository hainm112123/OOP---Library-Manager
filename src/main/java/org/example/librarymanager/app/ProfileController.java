package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;
import org.example.librarymanager.data.ServiceQuery;
import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.Category;
import org.example.librarymanager.models.ServiceData;
import org.example.librarymanager.models.User;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static java.time.DayOfWeek.*;

public class ProfileController extends ControllerWrapper {
    final static String[] Colors = {"#d3d3d3","#90ee90","#32cd32","#228B22","#006400"};
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
    private Label DateofBirth;
    @FXML
    private ScatterChart<Number,String> BorrowChart;
    @FXML
    private Label UserName;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetName();
        InitChart();

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
    public String getPointColor( int order ){
        return "-fx-shape: 'M -5 -5 L 5 -5 L 5 5 L -5 5 Z';" +
                "-fx-background-color: " + Colors[order > 4 ? 4 : order] + ";" +
                "-fx-padding: 8px;";
    }
    public String getOrderDay(LocalDate date){
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY:
                return "Mon";
            case TUESDAY:
                return "Tue";
            case WEDNESDAY:
                return "Wed";
            case THURSDAY:
                return "Thu";
            case FRIDAY:
                return "Fri";
            case SATURDAY:
                return "Sat";
            case SUNDAY:
                return "Sun";
            default:
                return "";
        }
    }
    /**
     * Init BorrowChart
     */
    public void InitChart(){
        List<ServiceData> ListBorrowDate = ServiceQuery.getInstance().getServiceData(getUser().getId());

        XYChart.Series<Number , String> series = new XYChart.Series<>();

        LocalDate today = LocalDate.now();
        LocalDate lastYearSameDay = today.minusYears(1);
        LocalDate currentDate = lastYearSameDay;

        int curIndexBorrowDate = 0;
        int orderColumn = 1;
        BorrowChart.getData().add(series);
        while (curIndexBorrowDate < ListBorrowDate.size() && ListBorrowDate.get(curIndexBorrowDate).getDate().isBefore(currentDate) )
            ++curIndexBorrowDate;

        while (!currentDate.isAfter(today)) {
            int orderColor = 0;
            if ( curIndexBorrowDate < ListBorrowDate.size() && ListBorrowDate.get(curIndexBorrowDate).getDate() == currentDate ) {
                ++curIndexBorrowDate;
                orderColor = ListBorrowDate.get(curIndexBorrowDate).getCount();
            }
            series.getData().addAll(new XYChart.Data<>(orderColumn, getOrderDay(currentDate)));
            XYChart.Data<Number, String> point = series.getData().get(series.getData().size()-1);
            if( point.getNode() != null ) {
                point.getNode().setStyle(getPointColor(orderColor));
            }
            if (getOrderDay(currentDate).equals("Sat")) {
                ++orderColumn;
            }
            currentDate = currentDate.plusDays(1);
        }

    }
    public void resetName(){
        FirstName.setText(getUser().getFirstname());
        LastName.setText(getUser().getLastname());
        Gender.setText(getUser().getGender());
        FName.setText(getUser().getFirstname());
        LName.setText(getUser().getLastname());
        DateofBirth.setText(getUser().getDateOfBirth().toString());
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
