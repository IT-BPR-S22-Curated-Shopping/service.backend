package bpr.service.backend.websocket.models;

public class IdentityDTO {
    private String uuid;

    public IdentityDTO() {
    }

    public IdentityDTO(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
