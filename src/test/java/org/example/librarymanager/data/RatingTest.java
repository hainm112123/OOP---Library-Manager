package org.example.librarymanager.data;

import org.example.librarymanager.app.HomeController;
import org.example.librarymanager.models.RecommendationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RatingTest {
    @Test
    public void testDataModel() {
        List<RecommendationData> dataModel = RatingQuery.getInstance().getDataModel();
        for (RecommendationData data : dataModel) {
            System.out.println(data);
        }
        Assertions.assertTrue(!dataModel.isEmpty());
    }
}
