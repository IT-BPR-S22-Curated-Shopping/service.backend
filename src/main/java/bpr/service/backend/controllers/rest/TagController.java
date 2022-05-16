package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.services.data.ICRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {
    private final ICRUDService<TagEntity> tagService;

    public TagController(@Autowired @Qualifier("TagService") ICRUDService<TagEntity> tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TagEntity> getAllTags() {
        return tagService.readAll();
    }

}
