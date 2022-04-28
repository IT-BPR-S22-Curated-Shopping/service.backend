package bpr.service.backend.services;

import bpr.service.backend.models.DeviceModel;

public interface IConnectionServiceCallback {
    void onMessageReceived(DeviceModel payload);
}
