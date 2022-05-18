package bpr.service.backend.services.data;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.persistence.repository.locationRepository.ILocationRepository;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import bpr.service.backend.util.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("LocationService")
public class LocationService implements ICRUDService<LocationEntity> {

    private final ILocationRepository locationRepository;

    private final IProductRepository productRepository;

    public LocationService(@Autowired ILocationRepository locationRepository, @Autowired IProductRepository productRepository) {
        this.locationRepository = locationRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<LocationEntity> readAll() {
        var locations = new ArrayList<LocationEntity>();
        locationRepository.findAll().forEach(locations::add);
        return locations;
    }

    @Override
    public LocationEntity readById(Long id) {
        if (locationRepository.findById(id).isPresent()) {
            return locationRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public LocationEntity create(LocationEntity entity) {
        return locationRepository.save(entity);
    }

    @Override
    public LocationEntity update(Long id, LocationEntity entity) {
        var databaseLocation = locationRepository.findById(id).get();
        databaseLocation.setProduct(entity.getProduct());
        databaseLocation.setPresentationDevices(entity.getPresentationDevices());
        databaseLocation.setIdentificationDevices(entity.getIdentificationDevices());
        return locationRepository.save(databaseLocation);
    }

    @SneakyThrows
    public LocationEntity updateWithDeviceList(Long id, List<IdentificationDeviceEntity> deviceList) {
        if (locationRepository.findById(id).isPresent()) {
            var databaseLocation = locationRepository.findById(id).get();

            databaseLocation.setIdentificationDevices(deviceList);

            return locationRepository.save(databaseLocation);
        }
        throw new NotFoundException(String.format("Location with ID: %s not found", id));
    }

    @SneakyThrows
    public LocationEntity updateWithProduct(Long id, ProductEntity product) {
        if (locationRepository.findById(id).isPresent()) {
            if (productRepository.existsById(product.getId())) {
                var databaseLocation = locationRepository.findById(id).get();

                databaseLocation.setProduct(product);

                return locationRepository.save(databaseLocation);
            }
            else throw new NotFoundException(String.format("Product with ID: %s not found", product.getId()));
        }
        throw new NotFoundException(String.format("Location with ID: %s not found", id));
    }

    @Override
    public void delete(Long id) {
        locationRepository.deleteById(id);
    }
}
