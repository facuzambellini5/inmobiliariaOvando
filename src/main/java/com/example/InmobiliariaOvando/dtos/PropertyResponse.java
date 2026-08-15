package com.example.InmobiliariaOvando.dtos;

import com.example.InmobiliariaOvando.enums.*;
import com.example.InmobiliariaOvando.models.PropertyPhoto;

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
        Short ambientes,
        Short bedrooms,
        Short bathrooms,
        Boolean garage,
        Boolean patio,
        BigDecimal surface,
        TerrainType terrainType,
        Instant createdAt,
        Instant updatedAt,
        List<PropertyPhoto> images
) {
}
