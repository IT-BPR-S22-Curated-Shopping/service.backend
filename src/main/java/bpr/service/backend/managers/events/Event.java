package bpr.service.backend.managers.events;

public enum Event {
    MQTT_MESSAGE_RECEIVED,
    MQTT_SUBSCRIBE,
    MQTT_PUBLISH,
    MQTT_UNSUBSCRIBE,
    ACTIVATE_DEVICE,
    DEACTIVATE_DEVICE,
    DEVICE_OFFLINE,
    DEVICE_ONLINE,
    DEVICE_READY,
    DEVICE_ACTIVE,
    INIT_DEVICE_COMM,
    DEVICE_CONNECTED,
    DEVICE_CONNECTED_ERROR,
    TELEMETRY_RECEIVED,
    DEVICE_STATUS_UPDATE,
    CUSTOMER_DETECTED,
    CUSTOMER_IDENTIFIED,
    CUSTOMER_LOCATED
}
