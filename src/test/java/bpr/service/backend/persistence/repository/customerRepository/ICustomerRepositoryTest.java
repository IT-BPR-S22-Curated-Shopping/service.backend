package bpr.service.backend.persistence.repository.customerRepository;

import bpr.service.backend.persistence.repository.entities.CustomerEntity;
import bpr.service.backend.persistence.repository.entities.TrackingIdEntity;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
class ICustomerRepositoryTest {

    @Autowired
    private ICustomerRepository customerRepository;


//    @Test
//    public void testThis() {
//        TrackingIdEntity tracking = new TrackingIdEntity();
//        tracking.setUuid("010d2108-0462-4f97-bab8-000000000001");
//        CustomerEntity customer = new CustomerEntity();
//        customer.setUuid(List.of(tracking));
//
//
//        CustomerEntity test = customerRepository.save(customer);
//
//        System.out.println("ICustomerRepositoryTest.test" + test.toString());
//
//
//
//
//        var test2= customerRepository.findAll();
//        System.out.println(Arrays.toString(new List[]{List.of(test2.iterator())}));
//    }
//

}