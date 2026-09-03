package com.example.InmobiliariaOvando.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import com.example.InmobiliariaOvando.dtos.PhotoResponse;
import com.example.InmobiliariaOvando.services.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/properties/{propertyId}/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping
    public ResponseEntity<List<PhotoResponse>> findByPropertyId(@PathVariable UUID propertyId){
        return ResponseEntity.ok(photoService.findByPropertyId(propertyId));
    }

    @PostMapping
    public ResponseEntity<PhotoResponse> upload(@PathVariable UUID propertyId,
                                                @RequestParam MultipartFile file) {
        PhotoResponse uploaded = photoService.upload(propertyId, file);
        URI location = URI.create("/api/properties/" + propertyId + "/photos/" + uploaded.id());
        return ResponseEntity.created(location).body(uploaded);
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(@PathVariable UUID propertyId, @PathVariable UUID photoId) {
        photoService.delete(propertyId, photoId);
        return ResponseEntity.noContent().build();
    }
}