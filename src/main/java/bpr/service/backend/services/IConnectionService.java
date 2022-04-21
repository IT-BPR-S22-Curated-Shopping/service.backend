package bpr.service.backend.services;

public interface IConnectionService {
    boolean connect();
    boolean disconnect();
    boolean sendMessage(Object payload);
}
