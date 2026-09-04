package com.example.InmobiliariaOvando.repositories.specifications;

import com.example.InmobiliariaOvando.dtos.PropertyFilterRequest;
import com.example.InmobiliariaOvando.enums.OperationType;
import com.example.InmobiliariaOvando.enums.PropertyStatus;
import com.example.InmobiliariaOvando.enums.PropertyType;
import com.example.InmobiliariaOvando.enums.Zone;
import com.example.InmobiliariaOvando.models.Property;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PropertySpecifications {

    private PropertySpecifications() {
    }

    public static Specification<Property> hasType(PropertyType type) {
        if (type == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Property> hasOperation(OperationType operation) {
        if (operation == null) {
            return null;
        }
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("operation"), operation),
                cb.equal(root.get("operation"), OperationType.AMBAS));
    }

    public static Specification<Property> hasZone(Zone zone) {
        if (zone == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("zone"), zone);
    }

    public static Specification<Property> hasStatus(PropertyStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Property> hasMinPrice(BigDecimal minPrice) {
        if (minPrice == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(relevantPrice(root, cb), minPrice);
    }

    public static Specification<Property> hasMaxPrice(BigDecimal maxPrice) {
        if (maxPrice == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(relevantPrice(root, cb), maxPrice);
    }

    private static Expression<BigDecimal> relevantPrice(
            jakarta.persistence.criteria.Root<Property> root,
            jakarta.persistence.criteria.CriteriaBuilder cb) {
        Expression<BigDecimal> salePrice = root.get("salePrice");
        Expression<BigDecimal> rentPrice = root.get("rentPrice");
        return cb.coalesce(salePrice, cb.coalesce(rentPrice, BigDecimal.ZERO));
    }

    /** Combina todos los filtros no nulos del DTO en una única Specification. */
    public static Specification<Property> build(PropertyFilterRequest filter) {
        List<Specification<Property>> specs = new ArrayList<>();
        specs.add(hasType(filter.type()));
        specs.add(hasOperation(filter.operation()));
        specs.add(hasZone(filter.zone()));
        specs.add(hasStatus(filter.status()));
        specs.add(hasMinPrice(filter.minPrice()));
        specs.add(hasMaxPrice(filter.maxPrice()));
        specs.removeIf(Objects::isNull);

        return Specification.allOf(specs);
    }
}