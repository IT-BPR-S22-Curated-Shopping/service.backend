package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.services.data.ICRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final ICRUDService<LocationEntity> locationService;

    public LocationController(@Autowired @Qualifier("LocationService") ICRUDService<LocationEntity> locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LocationEntity> getAllLocations() {
        return locationService.readAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LocationEntity getLocationById(@PathVariable("id") Long id) {
        return locationService.readById(id);
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public LocationEntity createLocation(@RequestBody LocationEntity location) {
//        return locationService.Create(location);
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationEntity createLocation(@RequestBody Map<String, String> map) {
        LocationEntity entity = new LocationEntity();

        if (map.containsKey("name")) {
            entity.setName(map.get("name"));
        }
        if (map.containsKey("productId")) {
            // not implemented, needs lookup
        }
        if (map.containsKey("deviceId")) {
            // not implemented, needs lookup
        }
        if (map.containsKey("presentationId")) {
            // not implemented, needs lookup
        }

        return locationService.create(entity);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LocationEntity updateLocation(@PathVariable("id") Long id, @RequestBody LocationEntity location) {
        return locationService.update(id, location);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLocation(@PathVariable("id") Long id) {
        locationService.delete(id);
    }

    // Source: https://www.baeldung.com/spring-boot-bean-validation
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}

