package bpr.service.backend.persistence.repository.detectionRepository;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import bpr.service.backend.models.entities.ProductEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDetectionRepository extends CrudRepository<DetectionSnapshotEntity, Long> {
    List<DetectionSnapshotEntity> findDetectionSnapshotEntitiesByProductIdAndTimestampBetween(Long productId, Long from, Long to);
    List<DetectionSnapshotEntity> findDetectionSnapshotEntitiesByLocationIdAndTimestampBetween(Long id, Long from, Long to);
    List<DetectionSnapshotEntity> findDetectionSnapshotEntitiesByLocationNameAndTimestampBetween(String locationName, Long from, Long to);
}
