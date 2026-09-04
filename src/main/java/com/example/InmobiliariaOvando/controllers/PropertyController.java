package com.example.InmobiliariaOvando.controllers;

import java.net.URI;

import java.util.Optional;
import java.util.UUID;

import com.example.InmobiliariaOvando.dtos.PropertyFilterRequest;
import com.example.InmobiliariaOvando.dtos.PropertyRequest;
import com.example.InmobiliariaOvando.dtos.PropertyResponse;
import com.example.InmobiliariaOvando.enums.PropertyStatus;
import com.example.InmobiliariaOvando.services.PropertyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest request) {
        PropertyResponse created = propertyService.create(request);
        return ResponseEntity.created(URI.create("/api/properties/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(propertyService.findById(id));
    }

    // Todos los filtros son opcionales: sin ninguno, se comporta como el
    // listado completo que ya usa el panel de admin. El sitio público
    // (property-browser) manda los que el visitante haya elegido.
    // @ModelAttribute bindea los query params (?type=...&operation=...)
    // directamente a los campos del record, sin tener que declarar un
    // @RequestParam por cada filtro.
    @GetMapping
    public ResponseEntity<Page<PropertyResponse>> findAll(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @ModelAttribute PropertyFilterRequest filter) {
        return ResponseEntity.ok(propertyService.findAll(pageable, filter));
    }

    @GetMapping("/status")
    public ResponseEntity<Page<PropertyResponse>> findByStatus (
            @RequestParam PropertyStatus status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.of(Optional.ofNullable(propertyService.findByStatus(status, pageable)));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody PropertyRequest request) {
        return ResponseEntity.ok(propertyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}