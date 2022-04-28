package bpr.service.backend.services;

public interface IConnectionServiceCallback {
    void onMessageReceived(String payload);
}
