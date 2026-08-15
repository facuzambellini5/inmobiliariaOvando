package com.example.InmobiliariaOvando.repositories;

import com.example.InmobiliariaOvando.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPropertyRepository extends JpaRepository<Property, UUID> {
}
