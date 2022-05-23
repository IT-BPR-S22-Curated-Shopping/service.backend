package bpr.service.backend.util.detectionAnalysisService;

import bpr.service.backend.models.dto.LocationAnalysisDto;
import bpr.service.backend.models.dto.ProductAnalysisDto;
import bpr.service.backend.util.exceptions.NotFoundException;

public interface IDetectionAnalysisService {

    ProductAnalysisDto productAnalysis(Long productId, Long from, Long to) throws NotFoundException;
    LocationAnalysisDto locationAnalysis(Long locationId, Long from, Long to) throws NotFoundException;
    LocationAnalysisDto locationAnalysis(String locationName, Long from, Long to) throws NotFoundException;


}
