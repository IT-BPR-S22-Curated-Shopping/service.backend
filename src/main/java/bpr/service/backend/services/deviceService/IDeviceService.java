package bpr.service.backend.services.deviceService;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;

import java.util.List;

public interface IDeviceService {

    List<IdentificationDeviceEntity> readAll();
    List<IdentificationDeviceEntity> readAllAvailable();
    IdentificationDeviceEntity readById(Long id);
}
