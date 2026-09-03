package com.example.InmobiliariaOvando.dtos;

import java.math.BigDecimal;

import com.example.InmobiliariaOvando.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PropertyRequest(

        @NotBlank(message = "El título es obligatorio")
        String title,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotNull(message = "El tipo de propiedad es obligatorio")
        PropertyType type,

        @NotNull(message = "La operación es obligatoria")
        OperationType operation,

        BigDecimal salePrice,

        BigDecimal rentPrice,

        @NotNull(message = "La moneda es obligatoria")
        Currency currency,

        @NotBlank(message = "La dirección es obligatoria")
        String address,

        Zone zone,

        @NotNull(message = "La latitud es obligatoria")
        Double lat,

        @NotNull(message = "La longitud es obligatoria")
        Double lng,

        // Campos de Casa/Departamento (nullable, solo aplican a esos tipos)
        Short rooms,
        Short bedrooms,
        Short bathrooms,
        Boolean hasGarage,
        Boolean hasPatio,

        // Campos de Terreno (nullable, solo aplica a ese tipo)
        BigDecimal surface,
        TerrainType terrainType,

        //TODO VER
        PropertyStatus status
) {
}