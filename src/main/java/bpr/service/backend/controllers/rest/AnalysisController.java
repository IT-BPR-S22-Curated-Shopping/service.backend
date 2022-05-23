package bpr.service.backend.controllers.rest;

import bpr.service.backend.util.detectionAnalysisService.IDetectionAnalysisService;
import bpr.service.backend.util.IDateTime;
import bpr.service.backend.util.ISerializer;
import bpr.service.backend.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final ISerializer serializer;
    private final IDateTime dateTime;
    private final IDetectionAnalysisService analysisService;

    public AnalysisController(
            @Autowired @Qualifier("JsonSerializer") ISerializer serializer,
            @Autowired @Qualifier("DetectionAnalysisService")IDetectionAnalysisService analysisService,
            @Autowired @Qualifier("DateTime")IDateTime dateTime) {
        this.serializer = serializer;
        this.dateTime = dateTime;
        this.analysisService = analysisService;
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<String> getProductAnalysisById(@PathVariable("id") Long id, @RequestParam Long from, @RequestParam Long to) {
        if (id <= 0) {
            return new ResponseEntity<>("Invalid id: Product id cannot be 0", HttpStatus.BAD_REQUEST);
        }
        var validationResponse = preformDateValidation(from, to);
        if (validationResponse != null) {
            return validationResponse;
        }

        try {
            return new ResponseEntity<>(serializer.toJson(analysisService.productAnalysis(id, from, to)), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to process the request at the moment. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/location/{id}")
    public ResponseEntity<String> getLocationAnalysisById(@PathVariable("id") Long id, @RequestParam Long from, @RequestParam Long to) {
        if (id <= 0) {
            return new ResponseEntity<>("Invalid id: Product id cannot be 0", HttpStatus.BAD_REQUEST);
        }
       var validationResponse = preformDateValidation(from, to);
       if (validationResponse != null) {
           return validationResponse;
       }

       try {
            return new ResponseEntity<>(serializer.toJson(analysisService.locationAnalysis(id, from, to)), HttpStatus.OK);
       } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
       } catch (Exception e) {
            return new ResponseEntity<>("Unable to process the request at the moment. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/location")
    public ResponseEntity<String> getLocationAnalysisByName(@RequestParam String name, @RequestParam Long from, @RequestParam Long to) {
        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("Invalid id: Product id cannot be 0", HttpStatus.BAD_REQUEST);
        }
        var validationResponse = preformDateValidation(from, to);
        if (validationResponse != null) {
            return validationResponse;
        }

        try {
            return new ResponseEntity<>(serializer.toJson(analysisService.locationAnalysis(name, from, to)), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to process the request at the moment. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> preformDateValidation(Long from, Long to) {
        if (from < dateTime.convertStringToEpochMillis("2022-01-01T00:00:00.0Z")) {
            return new ResponseEntity<>("Invalid timespan: From timestamp must be a value representing 2022-01-01T00:00:00.0Z or later", HttpStatus.BAD_REQUEST);
        }
        else if (to > dateTime.getEpochMillis()) {
            return new ResponseEntity<>("Invalid timespan: To must be before now", HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
