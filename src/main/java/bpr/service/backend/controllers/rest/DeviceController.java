package bpr.service.backend.controllers.rest;

import bpr.service.backend.services.deviceService.IDeviceService;
import bpr.service.backend.util.ISerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final IDeviceService deviceService;
    private final ISerializer serializer;

    public DeviceController(@Autowired @Qualifier("DeviceService") IDeviceService deviceService,
                            @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {
        this.deviceService = deviceService;
        this.serializer = serializer;
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<IdentificationDeviceEntity> getAllDevices() {
//        return deviceService.readAll();
//    }

    @GetMapping
    public ResponseEntity<String> getAllDevices() {
        return new ResponseEntity<>(serializer.toJson(deviceService.readAll()), HttpStatus.OK);
    }

//    @GetMapping("/available")
//    @ResponseStatus(HttpStatus.OK)
//    public List<IdentificationDeviceEntity> getAllAvailableDevices() {
//        return ((DeviceService) deviceService).readAllAvailable();
//    }

    @GetMapping("/available")
    public ResponseEntity<String> getAllAvailableDevices() {
        return new ResponseEntity<>(serializer.toJson(deviceService.readAllAvailable()), HttpStatus.OK);
    }
//    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public IdentificationDeviceEntity getDeviceById(@PathVariable("id") Long id) {
//        return deviceService.readById(id);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getDeviceById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(serializer.toJson(deviceService.readById(id)), HttpStatus.OK);
    }

}
