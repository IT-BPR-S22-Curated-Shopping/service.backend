package bpr.service.backend.services.recommender;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.recommender.ProductRecommendation;
import bpr.service.backend.models.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.PropertyChangeEvent;
import java.util.*;


public class Recommender implements IRecommender {

    private IEventManager eventManager;


    public Recommender(@Autowired IEventManager eventManager) {
        this.eventManager = eventManager;
        eventManager.addListener(Event.CUSTOMER_LOCATED, this::newCustomer);
    }

    private void newCustomer(PropertyChangeEvent propertyChangeEvent) {
        DetectionSnapshotEntity dse = (DetectionSnapshotEntity) propertyChangeEvent.getNewValue();
        var recommended = recommend(dse.getCustomer(), dse.getProduct());
        eventManager.invoke(Event.NEW_RECOMMENDATION, recommended);
    }

    @Override
    public List<ProductRecommendation> recommend(CustomerEntity customer, ProductEntity product) {
        return null;
    }
}
