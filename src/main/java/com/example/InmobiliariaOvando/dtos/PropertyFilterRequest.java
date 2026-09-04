package com.example.InmobiliariaOvando.dtos;

import com.example.InmobiliariaOvando.enums.OperationType;
import com.example.InmobiliariaOvando.enums.PropertyStatus;
import com.example.InmobiliariaOvando.enums.PropertyType;
import com.example.InmobiliariaOvando.enums.Zone;

import java.math.BigDecimal;

/**
 * Filtros opcionales del listado de propiedades (GET /api/properties).
 * Todos los campos pueden venir en null — sin ningún filtro, el listado
 * se comporta igual que antes (es lo que sigue usando el panel de admin,
 * que no manda ninguno de estos query params).
 */
public record PropertyFilterRequest(
        PropertyType type,
        OperationType operation,
        Zone zone,
        PropertyStatus status,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}