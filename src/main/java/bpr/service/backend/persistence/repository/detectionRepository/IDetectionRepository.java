package bpr.service.backend.persistence.repository.detectionRepository;

import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetectionRepository extends CrudRepository<DetectionSnapshotEntity, Long> {
}
