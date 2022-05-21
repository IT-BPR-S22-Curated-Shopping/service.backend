package bpr.service.backend.models.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VisitDetectionDto {
    private List<Long> timestamps;
    private Long durationMillis;

    public VisitDetectionDto() {
    }

    public VisitDetectionDto(List<Long> timestamps) {
        this.timestamps = new ArrayList<>(timestamps);
        durationMillis = timestamps.get(timestamps.size() - 1) - timestamps.get(0);
    }
}
