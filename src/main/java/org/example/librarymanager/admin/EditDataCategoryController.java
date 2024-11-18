package org.example.librarymanager.admin;

import javafx.application.Platform;
import org.example.librarymanager.data.CategoryQuery;
import org.example.librarymanager.models.Category;

import java.net.URL;
import java.util.ResourceBundle;

public class EditDataCategoryController extends EditDataController<Category> {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
//        System.out.println("EditDataCategoryController.initialize");
        dataAccessObject = CategoryQuery.getInstance();
        Platform.runLater(this::enableApply);
    }

}
