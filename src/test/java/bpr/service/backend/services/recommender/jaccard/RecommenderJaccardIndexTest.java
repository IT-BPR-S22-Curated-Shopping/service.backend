package bpr.service.backend.services.recommender.jaccard;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.models.entities.UuidEntity;
import bpr.service.backend.services.recommender.IRecommender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecommenderJaccardIndexTest {

    private IRecommender recommender;
    private CustomerEntity customer;
    private List<ProductEntity> products;

    @BeforeAll
    public void beforeAll() {
        List<TagEntity> allTags = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allTags.add(new TagEntity("tag#" + i));
        }

        List<UuidEntity> uuids = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            UuidEntity uuidEntity = new UuidEntity("ramdom uuid" + i);
            uuids.add(uuidEntity);
        }

        customer = new CustomerEntity(uuids, new ArrayList<>(List.of(allTags.get(0), allTags.get(1), allTags.get(9))));

        ProductEntity productEntity1 = new ProductEntity("Lamp1", "lamp name1", "", List.of(allTags.get(0), allTags.get(1), allTags.get(2)));
        ProductEntity productEntity2 = new ProductEntity("Lamp2", "lamp name2", "", List.of(allTags.get(0), allTags.get(3)));
        ProductEntity productEntity3 = new ProductEntity("Lamp3", "lamp name3", "", List.of(allTags.get(1), allTags.get(2)));
        ProductEntity productEntity4 = new ProductEntity("Lamp4", "lamp name4", "", List.of(allTags.get(3), allTags.get(2)));
        ProductEntity productEntity5 = new ProductEntity("Lamp5", "lamp name5", "", List.of(allTags.get(2), allTags.get(3)));
        ProductEntity productEntity6 = new ProductEntity("Lamp6", "lamp name6", "", List.of(allTags.get(0), allTags.get(1), allTags.get(9)));
        ProductEntity productEntity7 = new ProductEntity("Lamp7", "lamp name7", "", List.of(allTags.get(0), allTags.get(3), allTags.get(1), allTags.get(9)));
        products = new ArrayList<>(List.of(productEntity1, productEntity2, productEntity3, productEntity4, productEntity5, productEntity6, productEntity7));
    }


    @Test
    public void CheckCorrectJaccardIndex() {
        // arrange
        recommender = new RecommenderJaccardIndexMergedTags(null, null, products);
        var expectedBestProduct1 = products.get(6);
        var expectedBestProductScore = 1.0D;
        var expectedBestProduct2 = products.get(5);
        var expectedWorstProduct = products.get(4);

        // act
        var recommendation = recommender.recommend(customer, products.get(1));

        // assert
//        recommendation.forEach(x -> System.out.println("Product: " + x.getProduct().getName() + ", score: " + x.getScore()));

        assertEquals(products.size() - 1, recommendation.size());
        assertEquals(expectedBestProduct1.getName(), recommendation.get(0).getProduct().getName());
        assertEquals(expectedBestProductScore, recommendation.get(0).getScore());

        assertEquals(expectedBestProduct2.getName(), recommendation.get(1).getProduct().getName());
        assertEquals(expectedBestProductScore, recommendation.get(0).getScore());

        assertEquals(expectedWorstProduct.getName(), recommendation.get(5).getProduct().getName());
        assertEquals(.2D, recommendation.get(5).getScore());
    }


}