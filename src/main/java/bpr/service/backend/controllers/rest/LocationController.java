package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.services.deviceService.IDeviceService;
import bpr.service.backend.services.locationService.ILocationService;
import bpr.service.backend.services.locationService.LocationService;
import bpr.service.backend.services.productService.IProductService;
import bpr.service.backend.util.ISerializer;
import bpr.service.backend.util.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final ILocationService locationService;
    private final IDeviceService deviceService;
    private final IProductService productService;
    private final ISerializer serializer;

    public LocationController(@Autowired @Qualifier("LocationService") ILocationService locationService,
                              @Autowired @Qualifier("ProductService") IProductService productService,
                              @Autowired @Qualifier("DeviceService") IDeviceService deviceService,
                              @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {
        this.locationService = locationService;
        this.productService = productService;
        this.deviceService = deviceService;
        this.serializer = serializer;
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<LocationEntity> getAllLocations() {
//        return locationService.readAll();
//    }

    @GetMapping
    public ResponseEntity<String> getAllLocations() {
        return new ResponseEntity<>(serializer.toJson(locationService.readAll()), HttpStatus.OK);
    }

//    @GetMapping(value = "/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public LocationEntity getLocationById(@PathVariable("id") Long id) {
//        return locationService.readById(id);
//    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<String> getLocationById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(serializer.toJson(locationService.readById(id)), HttpStatus.OK);
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public LocationEntity createLocation(@RequestBody Map<String, String> map) {
//        LocationEntity entity = new LocationEntity();
//
//        if (map.containsKey(MAP_DEVICE_NAME)) {
//            if (!map.get("name").isBlank()) {
//                entity.setName(map.get("name"));
//            } else {
//                logger.info("Tried to create new location without name. (" + map.toString() + ")");
//                return null;
//            }
//        }
//
//        if (map.containsKey(MAP_DEVICE_PRODUCT_ID)) {
//            var product = productService.readById(Long.valueOf(map.get("productId")));
//            if (product != null){
//                entity.setProduct(product);
//            }
//        }
//
//        if (map.containsKey(MAP_DEVICE_IDS)) {
//
//            var listOfIds = map.get(MAP_DEVICE_IDS).split(", ");
//            System.out.println(Arrays.toString(listOfIds));
//            List<IdentificationDeviceEntity> devices = new ArrayList<>();
//            for (String listOfId : listOfIds) {
//                var device = deviceService.readById(Long.valueOf(listOfId));
//                if (device != null) {
//                    devices.add(device);
//                }
//            }
//            entity.setIdentificationDevices(devices);
//        }
//        if (map.containsKey(MAP_DEVICE_PRESENTATION_ID)) {
//            // not implemented, needs lookup
//        }
//
//
//        LocationEntity locationEntity = locationService.create(entity);
//        logger.info("Created location with: " + locationEntity.toString());
//        return locationEntity;
//    }

    @PostMapping
    public ResponseEntity<String> createLocation(@RequestBody Map<String, String> map) {
        LocationEntity entity = new LocationEntity();

        if (map.containsKey(MAP_DEVICE_NAME)) {
            if (!map.get("name").isBlank()) {
                entity.setName(map.get("name"));
            } else {
                logger.info("Tried to create new location without name. (" + map.toString() + ")");
                return new ResponseEntity<>("Tried to create new location without name. (" + map.toString() + ")", HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity<>(serializer.toJson(locationEntity), HttpStatus.CREATED);
    }

//    @PutMapping(value = "/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public LocationEntity updateLocation(@PathVariable("id") Long id, @RequestBody LocationEntity location) {
//        LocationEntity update = locationService.update(id, location);
//        logger.info("Updated location with: " + update.toString());
//        return update;
//    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<String> updateLocation(@PathVariable("id") Long id, @RequestBody LocationEntity location) {
        LocationEntity update = locationService.update(id, location);
        logger.info("Updated location with: " + update.toString());
        return new ResponseEntity<>(serializer.toJson(update), HttpStatus.OK);
    }


//    @PutMapping(value = "/{id}/devices")
//    @ResponseStatus(HttpStatus.OK)
//    public LocationEntity updateLocationTrackingDevices(@PathVariable("id") Long id, @NotNull @RequestBody List<IdentificationDeviceEntity> deviceList) {
//        LocationEntity locationEntity = ((LocationService) locationService).updateWithDeviceList(id, deviceList);
//        logger.info("Updated location as: " + locationEntity.toString());
//        return locationEntity;
//    }

    @PutMapping(value = "/{id}/devices")
    public ResponseEntity<String> updateLocationTrackingDevices(@PathVariable("id") Long id, @NotNull @RequestBody List<IdentificationDeviceEntity> deviceList) {
        LocationEntity locationEntity = ((LocationService) locationService).updateWithDeviceList(id, deviceList);
        logger.info("Updated location as: " + locationEntity.toString());
        return new ResponseEntity<>(serializer.toJson(locationEntity), HttpStatus.OK);
    }

//    @PutMapping(value = "/{id}/product")
//    @ResponseStatus(HttpStatus.OK)
//    public LocationEntity updateLocationProduct(@PathVariable("id") Long id, @NotNull @RequestBody ProductEntity productEntity) {
//
//        LocationEntity locationEntity = ((LocationService) locationService).updateWithProduct(id, productEntity);
//        logger.info("Updated location with product: " + locationEntity.toString());
//        return locationEntity;
//    }

    @PutMapping(value = "/{id}/product")
    public ResponseEntity<String> updateLocationProduct(@PathVariable("id") Long id, @NotNull @RequestBody ProductEntity productEntity) {

        LocationEntity locationEntity = ((LocationService) locationService).updateWithProduct(id, productEntity);
        logger.info("Updated location with product: " + locationEntity.toString());
        return new ResponseEntity<>(serializer.toJson(locationEntity), HttpStatus.OK);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteLocation(@PathVariable("id") Long id) {
        logger.info("Deleted location with id: " + id);
        locationService.delete(id);
        return new ResponseEntity<>(String.format("Location id %s successfully deleted.", id), HttpStatus.OK);
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

