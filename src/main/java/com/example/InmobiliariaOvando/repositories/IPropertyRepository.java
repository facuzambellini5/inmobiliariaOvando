package com.example.InmobiliariaOvando.repositories;

import com.example.InmobiliariaOvando.enums.PropertyStatus;
import com.example.InmobiliariaOvando.models.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IPropertyRepository extends JpaRepository<Property, UUID> {

    Page<Property> findByStatus(PropertyStatus propertyStatus, Pageable pageable );
}
