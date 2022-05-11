package bpr.service.backend.controllers.rest;

import bpr.service.backend.data.entities.TrackerEntity;
import bpr.service.backend.services.data.ICRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final ICRUDService<TrackerEntity> deviceService;

    public DeviceController(@Autowired @Qualifier("DeviceService") ICRUDService<TrackerEntity> deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TrackerEntity> getAllDevices() {
        return deviceService.ReadAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrackerEntity getDeviceById(@PathVariable("id") Long id) {
        return deviceService.ReadById(id);
    }
}
