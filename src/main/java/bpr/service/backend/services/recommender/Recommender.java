package bpr.service.backend.services.recommender;

import bpr.service.backend.managers.events.IEventManager;
import org.springframework.beans.factory.annotation.Autowired;

public class Recommender implements IRecommender {

    private IEventManager eventManager;

    public Recommender(@Autowired IEventManager eventManager) {
        this.eventManager = eventManager;
    }
}
