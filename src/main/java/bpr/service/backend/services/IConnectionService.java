package bpr.service.backend.services;

public interface IConnectionService {
    void connect() throws Throwable;
    void disconnect() throws Throwable;
}
