package bpr.service.backend.services;

import bpr.service.backend.models.mqtt.DeviceModel;

public class ConnectionServiceCallbackImpl implements IConnectionServiceCallback {
    @Override
    public void onMessageReceived(DeviceModel payload) {
        System.out.println("IConnectionServiceCallbackImpl.invoke: " + payload.toString());
    }
}
