package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.services.data.ICRUDService;
import bpr.service.backend.services.data.LocationService;
import bpr.service.backend.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private static final String MAP_DEVICE_NAME = "name";
    private static final String MAP_DEVICE_PRODUCT_ID = "productId";
    private static final String MAP_DEVICE_IDS = "deviceIds";
    private static final String MAP_DEVICE_PRESENTATION_ID = "presentationId";

    private final ICRUDService<LocationEntity> locationService;
    private final ICRUDService<IdentificationDeviceEntity> deviceService;
    private final ICRUDService<ProductEntity> productService;

    public LocationController(@Autowired @Qualifier("LocationService") ICRUDService<LocationEntity> locationService,
                              @Autowired @Qualifier("ProductService") ICRUDService<ProductEntity> productService,
                              @Autowired @Qualifier("DeviceService") ICRUDService<IdentificationDeviceEntity> deviceService) {
        this.locationService = locationService;
        this.productService = productService;
        this.deviceService = deviceService;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationEntity createLocation(@RequestBody Map<String, String> map) {
        LocationEntity entity = new LocationEntity();

        if (map.containsKey(MAP_DEVICE_NAME)) {
            if (!map.get("name").isBlank()) {
                entity.setName(map.get("name"));
            } else {
                return null;
            }
        }

        if (map.containsKey(MAP_DEVICE_PRODUCT_ID)) {
            var product = productService.readById(Long.valueOf(map.get("productId")));
            if (product != null)
                entity.setProduct(product);
        }

        if (map.containsKey(MAP_DEVICE_IDS)) {

            var listOfIds = map.get(MAP_DEVICE_IDS).split(", ");
            List<IdentificationDeviceEntity> devices = new ArrayList<>();
            for (String listOfId : listOfIds) {
                var device = deviceService.readById(Long.valueOf(listOfId));
                if (device != null) {
                    devices.add(device);
                }
            }
            entity.setIdentificationDevices(devices);
        }
        if (map.containsKey(MAP_DEVICE_PRESENTATION_ID)) {
            // not implemented, needs lookup
        }

        return locationService.create(entity);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LocationEntity updateLocation(@PathVariable("id") Long id, @RequestBody LocationEntity location) {
        return locationService.update(id, location);
    }

    @PutMapping(value = "/{id}/devices")
    @ResponseStatus(HttpStatus.OK)
    public LocationEntity updateLocationTrackingDevices(@PathVariable("id") Long id, @NotNull @RequestBody List<IdentificationDeviceEntity> deviceList) {
        return ((LocationService) locationService).updateWithDeviceList(id, deviceList);
    }

    @PutMapping(value = "/{id}/product")
    @ResponseStatus(HttpStatus.OK)
    public LocationEntity updateLocationProduct(@PathVariable("id") Long id, @NotNull @RequestBody ProductEntity productEntity) {
        return ((LocationService) locationService).updateWithProduct(id, productEntity);
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

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException exception) {
        return exception.getMessage();
    }
}

