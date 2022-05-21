package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.services.data.ICRUDService;
import bpr.service.backend.services.data.LocationService;
import bpr.service.backend.util.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
                logger.info("Tried to create new location without name. (" + map.toString() + ")");
                return null;
            }
        }

        if (map.containsKey(MAP_DEVICE_PRODUCT_ID)) {
            var product = productService.readById(Long.valueOf(map.get("productId")));
            if (product != null){
                entity.setProduct(product);
            }
        }

        if (map.containsKey(MAP_DEVICE_IDS)) {

            var listOfIds = map.get(MAP_DEVICE_IDS).split(", ");
            System.out.println(Arrays.toString(listOfIds));
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


        LocationEntity locationEntity = locationService.create(entity);
        logger.info("Created location with: " + locationEntity.toString());
        return locationEntity;
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LocationEntity updateLocation(@PathVariable("id") Long id, @RequestBody LocationEntity location) {
        LocationEntity update = locationService.update(id, location);
        logger.info("Updated location with: " + update.toString());
        return update;
    }

    @PutMapping(value = "/{id}/devices")
    @ResponseStatus(HttpStatus.OK)
    public LocationEntity updateLocationTrackingDevices(@PathVariable("id") Long id, @NotNull @RequestBody List<IdentificationDeviceEntity> deviceList) {
        LocationEntity locationEntity = ((LocationService) locationService).updateWithDeviceList(id, deviceList);
        logger.info("Updated location as: " + locationEntity.toString());
        return locationEntity;
    }

    @PutMapping(value = "/{id}/product")
    @ResponseStatus(HttpStatus.OK)
    public LocationEntity updateLocationProduct(@PathVariable("id") Long id, @NotNull @RequestBody ProductEntity productEntity) {

        LocationEntity locationEntity = ((LocationService) locationService).updateWithProduct(id, productEntity);
        logger.info("Updated location with product: " + locationEntity.toString());
        return locationEntity;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLocation(@PathVariable("id") Long id) {
        logger.info("Deleted location with id: " + id);
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

