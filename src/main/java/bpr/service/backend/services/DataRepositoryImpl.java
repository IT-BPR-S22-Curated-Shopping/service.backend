package bpr.service.backend.services;

import bpr.service.backend.models.sql.CustomerEntity;
import bpr.service.backend.models.sql.TagEntity;
import bpr.service.backend.persistence.sql.CustomerRepository;
import bpr.service.backend.persistence.sql.TagRepository;
import bpr.service.backend.services.sql.ICustomerRepository;
import bpr.service.backend.services.sql.ITagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class DataRepositoryImpl implements ICustomerRepository, ITagRepository {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TagRepository tagRepository;


    @Override
    public CustomerEntity addCustomer(CustomerEntity customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Iterable<CustomerEntity> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public CustomerEntity findCustomerByUUID(String uuid) {
        return customerRepository.findByUuid(uuid);
    }

    @Override
    public Optional<CustomerEntity> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }



    @Override
    public TagEntity addTag(TagEntity tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Iterable<TagEntity> findAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public TagEntity findByTag(String tag) {
        return tagRepository.findByTag(tag);
    }

    @Override
    public Optional<TagEntity> findTagById(Long id) {
        return tagRepository.findById(id);
    }



}
