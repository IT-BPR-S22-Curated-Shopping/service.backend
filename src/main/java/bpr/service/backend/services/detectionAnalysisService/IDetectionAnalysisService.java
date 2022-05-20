package bpr.service.backend.services.detectionAnalysisService;

import bpr.service.backend.models.dto.ProductAnalysisDto;

public interface IDetectionAnalysisService {

    ProductAnalysisDto productAnalysis(Long productId, Long from, Long to);
}
