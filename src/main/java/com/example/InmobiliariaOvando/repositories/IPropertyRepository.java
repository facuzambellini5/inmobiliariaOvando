package com.example.InmobiliariaOvando.repositories;

import com.example.InmobiliariaOvando.enums.PropertyStatus;
import com.example.InmobiliariaOvando.models.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// JpaSpecificationExecutor habilita findAll(Specification, Pageable):
// necesario para armar el query de filtros dinámicos (tipo, operación,
// zona, precio) sin tener que escribir un metodo distinto por cada
// combinación posible de filtros.
@Repository
public interface IPropertyRepository extends JpaRepository<Property, UUID>,
        JpaSpecificationExecutor<Property> {

    Page<Property> findByStatus(PropertyStatus propertyStatus, Pageable pageable );
}