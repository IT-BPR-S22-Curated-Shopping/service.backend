package bpr.service.backend.services.data;

import bpr.service.backend.data.entities.LocationEntity;
import bpr.service.backend.persistence.repository.locationRepository.ILocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("LocationService")
public class LocationService implements ICRUDService<LocationEntity> {

    private final ILocationRepository locationRepository;

    public LocationService(@Autowired ILocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<LocationEntity> ReadAll() {
        var locations = new ArrayList<LocationEntity>();
        locationRepository.findAll().forEach(locations::add);
        return locations;
    }

    @Override
    public LocationEntity ReadById(Long id) {
        if (locationRepository.findById(id).isPresent()) {
            return locationRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public LocationEntity Create(LocationEntity entity) {
        return locationRepository.save(entity);
    }

    @Override
    public LocationEntity Update(Long id, LocationEntity entity) {
        var databaseLocation = locationRepository.findById(id).get();
        databaseLocation.setProduct(entity.getProduct());
        databaseLocation.setPresentationDevices(entity.getPresentationDevices());
        databaseLocation.setTrackingDevices(entity.getTrackingDevices());
        return locationRepository.save(databaseLocation);
    }

    @Override
    public void Delete(Long id) {
        locationRepository.deleteById(id);
    }
}
