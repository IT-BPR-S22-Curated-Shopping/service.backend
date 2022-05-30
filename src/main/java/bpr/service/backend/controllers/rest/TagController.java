package bpr.service.backend.controllers.rest;

import bpr.service.backend.services.tagService.ITagService;
import bpr.service.backend.util.ISerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {
    private final ITagService tagService;
    private final ISerializer serializer;

    public TagController(@Autowired @Qualifier("TagService") ITagService tagService,
                         @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {
        this.tagService = tagService;
        this.serializer = serializer;
    }

    @GetMapping
    public ResponseEntity<String> getAllTags() {
        return new ResponseEntity<>(serializer.toJson(tagService.readAll()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createTags(List<String> tags) {
        ResponseEntity<String> response;

        if (tags != null && tags.size() > 0) {
            var tagEntities = tagService.createTags(tags);
            if (tagEntities != null) {
                response = new ResponseEntity<>(serializer.toJson(tagEntities), HttpStatus.CREATED);
            } else {
                response = new ResponseEntity<>("Was unable to create the provided tags. Unknown error.", HttpStatus.CONFLICT);
            }
        } else {
            response = new ResponseEntity<>("Tags must be included to be able to create tags.", HttpStatus.BAD_REQUEST);
        }
        return response;

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable("id") Long id) {
        ResponseEntity<String> response;

        if (id != 0) {
            tagService.delete(id);
            response = new ResponseEntity<>(String.format("Tag id %s successfully deleted", id), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Invalid tag id", HttpStatus.BAD_REQUEST);
        }

        return response;
    }

}
