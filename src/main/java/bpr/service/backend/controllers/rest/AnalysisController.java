package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.services.detectionAnalysisService.IDetectionAnalysisService;
import bpr.service.backend.util.ISerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private ISerializer serializer;
    private IDetectionAnalysisService analysisService;

    public AnalysisController(
            @Autowired @Qualifier("JsonSerializer") ISerializer serializer,
            @Autowired @Qualifier("DetectionAnalysisService")IDetectionAnalysisService analysisService) {
        this.serializer = serializer;
        this.analysisService = analysisService;
    }

    @GetMapping("/product/{id}/{from}/{to}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getProductAnalysis(@PathVariable("id") Long id, @PathVariable("from") Long from, @PathVariable("to") Long to) {
        System.out.println("GOT ANALYSIS REQUEST");

        var payload = analysisService.productAnalysis(id, from, to);

        return new ResponseEntity<>(serializer.toJson(payload), HttpStatus.OK);
    }
}
