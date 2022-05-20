package bpr.service.backend.services.recommender;

import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.models.entities.UuidEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class Recommender implements IRecommender {

    private IEventManager eventManager;

    public Recommender(@Autowired IEventManager eventManager) {
        this.eventManager = eventManager;
    }




    public static void main(String[] args) {

        // create tags

        List<TagEntity> allTags = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            allTags.add(new TagEntity("tag#" + i));
        }



        // create customer
        List<UuidEntity> uuids = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            UuidEntity uuidEntity = new UuidEntity("ramdom uuid" + i);
            uuids.add(uuidEntity);
        }

        CustomerEntity customerEntity1 = new CustomerEntity(uuids, new ArrayList<>(List.of(allTags.get(0), allTags.get(1))));
        CustomerEntity customerEntity2 = new CustomerEntity(uuids, new ArrayList<>(List.of(allTags.get(0), allTags.get(1), allTags.get(2))));
        CustomerEntity customerEntity3 = new CustomerEntity(uuids, new ArrayList<>(List.of(allTags.get(0), allTags.get(1))));

//        System.out.println(customerEntity1.toString());


        ProductEntity productEntity1 = new ProductEntity("Lamp1", "lamp name1", "", List.of(allTags.get(0), allTags.get(1), allTags.get(2)));
        ProductEntity productEntity2 = new ProductEntity("Lamp2", "lamp name2", "", List.of(allTags.get(0), allTags.get(3)));
        ProductEntity productEntity3 = new ProductEntity("Lamp2", "lamp name2", "", List.of(allTags.get(1), allTags.get(2)));
        List<ProductEntity> smallProduct = new ArrayList<>();
        smallProduct.add(productEntity1);
        smallProduct.add(productEntity2);
        smallProduct.add(productEntity3);

//        List<ProductEntity> products = new ArrayList<>();
//        for (int i = 0; i < 100 ; i++) {
//            products.add(new ProductEntity("Lamp"+i, "Lamp name"+i, "", List.of(allTags.get(i)), null));
//        }

        Map<String, List<String>> table = new HashMap<>();

        for (int i = 0; i < customerEntity1.getTags().size(); i++) {
            for (int j = 0; j < smallProduct.size(); j++) {
                for (int k = 0; k < smallProduct.get(j).getTags().size(); k++) {
                    if (customerEntity1.getTags().get(i).getTag().equals(smallProduct.get(j).getTags().get(k).getTag())) {
                        var productName = smallProduct.get(j).getName();
                        if (table.containsKey(customerEntity1.getTags().get(i).getTag())){
                            var list = (ArrayList<String>) table.get(customerEntity1.getTags().get(i).getTag());
                            if (list.stream().noneMatch(x -> x.equals(productName))) {
                                list.add(productName);
                                table.put(customerEntity1.getTags().get(i).getTag(), list);
                            }
                        } else {
                            table.put(customerEntity1.getTags().get(i).getTag(), new ArrayList<>(List.of(productName)));
                        }
                    }
                }
            }
        }

        System.out.println(table.toString());




    }


}
