package bpr.service.backend.services;

import bpr.service.backend.models.sql.CustomerEntity;
import bpr.service.backend.models.sql.TagEntity;
import bpr.service.backend.persistence.sql.CustomerRepository;
import bpr.service.backend.persistence.sql.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class DataRepositoryImplTest {

    @InjectMocks
    private DataRepositoryImpl dataRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TagRepository tagRepository;


    @BeforeEach
    public void beforeEach() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void canFindCustomerByUUID() {
        // arrange
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        String uuid = UUID.randomUUID().toString();
        customer.setUuid(uuid);

        TagEntity t = new TagEntity();
        t.setId(1L);
        t.setTag("Test1");
        customer.setTags(Collections.singletonList(t));

        when(customerRepository.findByUuid(anyString())).thenReturn(customer);

        // act
        var response = dataRepository.findCustomerByUUID(uuid);

        // assert
        assertNotNull(response);
        assertEquals(response, customer);
    }


    @Test
    public void canFindTagByName() {
        // arrange
        String tagName1 = "testTag1";
        String tagName2 = "testTag2";

        TagEntity t1 = new TagEntity();
        t1.setId(1L);
        t1.setTag(tagName1);
        TagEntity t2 = new TagEntity();
        t2.setId(1L);
        t2.setTag(tagName2);

        when(tagRepository.findByTag(tagName1)).thenReturn(t1);
        when(tagRepository.findByTag(tagName2)).thenReturn(t2);
        when(tagRepository.findByTag("does_not_exist")).thenReturn(null);
        // act

        var response1 = dataRepository.findByTag(tagName1);
        var response2 = dataRepository.findByTag(tagName2);
        var response3 = dataRepository.findByTag("dddd");


        // assert
        assertNotNull(response1);
        assertNotNull(response2);
        assertNull(response3);

        assertEquals(response1, t1);
        assertEquals(response2, t2);

    }

}