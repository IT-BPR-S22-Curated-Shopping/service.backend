package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.services.data.DeviceService;
import bpr.service.backend.services.data.ICRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final ICRUDService<IdentificationDeviceEntity> deviceService;

    public DeviceController(@Autowired @Qualifier("DeviceService") ICRUDService<IdentificationDeviceEntity> deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<IdentificationDeviceEntity> getAllDevices() {
        return deviceService.readAll();
    }

    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    public List<IdentificationDeviceEntity> getAllAvailableDevices() {
        return ((DeviceService) deviceService).readAllAvailable();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IdentificationDeviceEntity getDeviceById(@PathVariable("id") Long id) {
        return deviceService.readById(id);
    }

}
