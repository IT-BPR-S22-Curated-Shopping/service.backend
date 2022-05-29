package bpr.service.backend.controllers.websocket;

import bpr.service.backend.Application;
import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.RecommendationDto;
import bpr.service.backend.persistence.repository.customerRepository.ICustomerRepository;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import bpr.service.backend.services.recommender.IRecommender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
class WebSocketHandlerIntegrationTest {
    
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        WebSocketTestService service = applicationContext.getBean(WebSocketTestService.class);

        service.createRecommendation();
    }
}

@Service
class WebSocketTestService {


    @Autowired private ICustomerRepository customerRepository;
    @Autowired private IProductRepository productRepository;
    @Autowired private IRecommender recommender;
    @Autowired private IEventManager eventManager;

    public void createRecommendation() {
        // send event
        sendEvent(30000, 3L);
    }

    private void sendEvent(int msWait, Long locationId) {

        // find a customer
        var customerEntity = customerRepository.findById(3L).orElse(null);
        // find product
        var productEntity = productRepository.findById(2L).orElse(null);


        try {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Connect presentation device to location id: " + locationId + " within " + msWait / 1000 + " seconds.");
            System.out.println("--------------------------------------------------------------------------------");
            TimeUnit.MILLISECONDS.sleep(msWait);
            System.out.println("--------------------------------------------------------------------------------");
            var recommendation = recommender.recommend(customerEntity, productEntity, 10);
            RecommendationDto payload = new RecommendationDto(customerEntity, productEntity, locationId, recommendation);
            System.out.println("Sending recommendation to location " + locationId + " with content: " + payload.toString());
            System.out.println("--------------------------------------------------------------------------------");
            eventManager.invoke(Event.NEW_RECOMMENDATION, payload);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}