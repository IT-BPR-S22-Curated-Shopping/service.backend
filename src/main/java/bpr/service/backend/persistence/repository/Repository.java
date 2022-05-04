package bpr.service.backend.persistence.repository;

import bpr.service.backend.persistence.repository.customerRepository.ICustomerRepository;
import bpr.service.backend.persistence.repository.tagRepository.ITagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class Repository implements IRepository {

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private ITagRepository tagRepository;

    @Override
    public ICustomerRepository customer() {
        return customerRepository;
    }

    @Override
    public ITagRepository tag() {
        return tagRepository;
    }


}
