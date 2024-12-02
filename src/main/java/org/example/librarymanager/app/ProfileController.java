package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.ServiceQuery;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.example.librarymanager.components.Avatar;
import org.example.librarymanager.services.Backblaze;

import org.example.librarymanager.data.UserQuery;
import org.example.librarymanager.models.ServiceData;
import org.example.librarymanager.models.User;

import java.io.File;
import java.net.URL;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.time.format.DateTimeFormatter;

import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProfileController extends ControllerWrapper {
    final static String[] Colors = {"#d3d3d3","#90ee90","#32cd32","#228B22","#006400"};
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
    private ScatterChart<Number,String> BorrowChart;
    @FXML
    private MFXComboBox<String> GenderField;
    @FXML
    private MFXDatePicker DateOfBirthField;
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

        InitChart();

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
        executor = Executors.newSingleThreadExecutor();
        Future<List<ServiceData>> future = executor.submit(() -> ServiceQuery.getInstance().getServiceData(getUser().getId()));
        List<ServiceData> ListBorrowDate;
        try {
            ListBorrowDate = future.get();
        } catch (Exception e) {
            ListBorrowDate = new ArrayList<>();
            e.printStackTrace();
        }

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
            if ( curIndexBorrowDate < ListBorrowDate.size() && ListBorrowDate.get(curIndexBorrowDate).getDate().isEqual(currentDate) ) {
                orderColor = ListBorrowDate.get(curIndexBorrowDate).getCount() ;
                ++curIndexBorrowDate;
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
        if (getUser().getDateOfBirth() != null) {
            DateOfBirth.setText(getUser().getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            DateOfBirth.setText("n/a");
        }
        FName.setText(getUser().getFirstname());
        LName.setText(getUser().getLastname());
        if (getUser().getGender() != null && getUser().getGender().equals("Male")) {
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
