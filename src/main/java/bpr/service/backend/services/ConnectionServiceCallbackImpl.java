package bpr.service.backend.services;

public class ConnectionServiceCallbackImpl implements IConnectionServiceCallback {
    @Override
    public void invoke(String payload) {
        System.out.println("IConnectionServiceCallbackImpl.invoke: " + payload);
    }
}
