package bpr.service.backend.services;

import bpr.service.backend.models.DeviceModel;

public class ConnectionServiceCallbackImpl implements IConnectionServiceCallback {
    @Override
    public void onMessageReceived(DeviceModel payload) {
        System.out.println("IConnectionServiceCallbackImpl.invoke: " + payload.toString());
    }
}
