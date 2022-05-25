package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.services.locationService.ILocationService;
import bpr.service.backend.services.locationService.LocationService;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ILocationService locationService;

    private final ISerializer serializer;

    public LocationController(@Autowired @Qualifier("LocationService") ILocationService locationService,
                              @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {
        this.locationService = locationService;
        this.serializer = serializer;
    }

    @GetMapping
    public ResponseEntity<String> getAllLocations() {
        return new ResponseEntity<>(serializer.toJson(locationService.readAll()), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<String> getLocationById(@PathVariable("id") Long id) {
        ResponseEntity<String> response;
        if (id != 0) {
            LocationEntity location = locationService.readById(id);
            if (location != null) {
                response = new ResponseEntity<>(serializer.toJson(location), HttpStatus.OK);
            } else {
                response = new ResponseEntity<>("Could not find a product with given ID: " + id, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>("Invalid ID: location id cannot be 0.", HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    @PostMapping
    public ResponseEntity<String> createLocation(@RequestBody Map<String, String> map) {
        if (!map.containsKey(MAP_DEVICE_NAME)) {
            return new ResponseEntity<>("Request must contain property 'name'", HttpStatus.BAD_REQUEST);
        }
        else if (!map.containsKey(MAP_DEVICE_PRODUCT_ID)) {
            return new ResponseEntity<>("Request must contain property 'productId'", HttpStatus.BAD_REQUEST);
        }
        else if (!map.containsKey(MAP_DEVICE_IDS)) {
            return new ResponseEntity<>("Request must contain property 'deviceIds'", HttpStatus.BAD_REQUEST);
        }

        if (map.get(MAP_DEVICE_NAME).isEmpty()) {
            return new ResponseEntity<>("Name cannot be empty.", HttpStatus.BAD_REQUEST);
        }
        String name = map.get(MAP_DEVICE_NAME);

        var productId = 0L;
        if (!map.get(MAP_DEVICE_PRODUCT_ID).isEmpty()) {
            productId = Long.parseLong(map.get(MAP_DEVICE_PRODUCT_ID));
        }

        var ids = new ArrayList<String>();
        if(!map.get(MAP_DEVICE_IDS).isEmpty()) {
            ids.addAll(List.of(map.get(MAP_DEVICE_IDS).split(", ")));
        }
//        List<String> ids = new ArrayList<>(Arrays.asList(map.get(MAP_DEVICE_IDS).split(", ")));

        var created = locationService.createLocation(name, productId, ids);

        if (created == null) {
            return new ResponseEntity<>("Error creating location: Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(serializer.toJson(created), HttpStatus.CREATED);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<String> updateLocation(@PathVariable("id") Long id, @RequestBody LocationEntity location) {
        ResponseEntity<String> response;

        if (id != 0) {
            if (location != null) {
                response = new ResponseEntity<>(serializer.toJson(locationService.update(id, location)), HttpStatus.OK);
                logger.info("Updated location with id " + id);
            } else {
                response = new ResponseEntity<>("Cannot update location. Must contain location", HttpStatus.BAD_REQUEST);
            }
        } else {
            response = new ResponseEntity<>("Cannot update location. Invalid ID: " + id, HttpStatus.BAD_REQUEST);
        }

        return response;
    }


    @PutMapping(value = "/{id}/devices")
    public ResponseEntity<String> updateLocationTrackingDevices(@PathVariable("id") Long id, @NotNull @RequestBody List<IdentificationDeviceEntity> deviceList) {
        ResponseEntity<String> response;

        if (id != 0) {
            LocationEntity locationEntity = locationService.updateWithDeviceList(id, deviceList);
            logger.info("Updated location as: " + locationEntity.toString());
            response = new ResponseEntity<>(serializer.toJson(locationEntity), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Cannot update. Invalid ID: " + id, HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    @PutMapping(value = "/{id}/product")
    public ResponseEntity<String> updateLocationProduct(@PathVariable("id") Long id, @NotNull @RequestBody ProductEntity productEntity) {
        ResponseEntity<String> response;
        if (id != 0) {
            System.out.println("LocationController.updateLocationProduct: " + productEntity);
            LocationEntity locationEntity = ((LocationService) locationService).updateWithProduct(id, productEntity);
            logger.info("Updated location with product: " + locationEntity.toString());
            response = new ResponseEntity<>(serializer.toJson(locationEntity), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Cannot update. Invalid ID: " + id, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteLocation(@PathVariable("id") Long id) {
        ResponseEntity<String> response;
        if (id != 0) {
            logger.info("Deleted location with id: " + id);
            locationService.delete(id);
            response = new ResponseEntity<>(String.format("Location id %s successfully deleted.", id), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Cannot delete. Invalid ID: " + id, HttpStatus.BAD_REQUEST);
        }

        return response;
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

