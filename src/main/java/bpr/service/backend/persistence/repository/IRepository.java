package bpr.service.backend.persistence.repository;

import bpr.service.backend.persistence.repository.customerRepository.ICustomerRepository;
import bpr.service.backend.persistence.repository.tagRepository.ITagRepository;

public interface IRepository {

    ICustomerRepository customer();
    ITagRepository tag();


}
