package com.example.InmobiliariaOvando.dtos;

import com.example.InmobiliariaOvando.enums.*;
import com.example.InmobiliariaOvando.models.Property;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PropertyResponse(
        UUID id,
        String title,
        String description,
        PropertyType type,
        OperationType operation,
        BigDecimal salePrice,
        BigDecimal rentPrice,
        Currency currency,
        String address,
        Zone zone,
        Double lat,
        Double lng,
        PropertyStatus status,
        Short rooms,
        Short bedrooms,
        Short bathrooms,
        Boolean hasGarage,
        Boolean hasPatio,
        BigDecimal surface,
        TerrainType terrainType,
        Instant createdAt,
        Instant updatedAt,
        List<PhotoResponse> images
) {

    public PropertyResponse(Property property) {
        this(
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                property.getType(),
                property.getOperation(),
                property.getSalePrice(),
                property.getRentPrice(),
                property.getCurrency(),
                property.getAddress(),
                property.getZone(),
                property.getLat(),
                property.getLng(),
                property.getStatus(),
                property.getRooms(),
                property.getBedrooms(),
                property.getBathrooms(),
                property.getHasGarage(),
                property.getHasPatio(),
                property.getSurface(),
                property.getTerrainType(),
                property.getCreatedAt(),
                property.getUpdatedAt(),
                property.getPhotos().stream().map(PhotoResponse::new).toList()
        );
    }
}