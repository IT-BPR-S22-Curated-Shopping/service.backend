package bpr.service.backend.services;

public class ConnectionServiceCallbackImpl implements IConnectionServiceCallback {
    @Override
    public void onMessageReceived(String payload) {
        System.out.println("IConnectionServiceCallbackImpl.invoke: " + payload);
    }
}
