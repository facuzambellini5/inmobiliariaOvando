package com.example.InmobiliariaOvando.services;

import java.util.List;
import java.util.UUID;

import com.example.InmobiliariaOvando.dtos.PropertyRequest;
import com.example.InmobiliariaOvando.dtos.PropertyResponse;
import com.example.InmobiliariaOvando.enums.PropertyType;
import com.example.InmobiliariaOvando.exceptions.EntityNotFoundException;
import com.example.InmobiliariaOvando.models.Property;
import com.example.InmobiliariaOvando.repositories.IPropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertyService {

    private final IPropertyRepository propertyRepo;

    public PropertyService(IPropertyRepository propertyRepo) {
        this.propertyRepo = propertyRepo;
    }

    @Transactional
    public PropertyResponse create(PropertyRequest request) {
        Property property = new Property();
        applyRequest(property, request);

        return new PropertyResponse(propertyRepo.save(property));
    }

    @Transactional(readOnly = true)
    public PropertyResponse findById(UUID id) {
        return new PropertyResponse(propertyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property", "id", id.toString())));
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> findAll() {
        return propertyRepo.findAll()
                .stream()
                .map(PropertyResponse::new)
                .toList();
    }

    @Transactional
    public PropertyResponse update(UUID id, PropertyRequest request) {
        Property property = propertyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property", "id", id.toString()));
        applyRequest(property, request);

        return new PropertyResponse(propertyRepo.save(property));
    }

    @Transactional
    public void delete(UUID id) {
        if (!propertyRepo.existsById(id)) {
            throw new EntityNotFoundException("Property", "id", id.toString());
        }
        propertyRepo.deleteById(id);
    }

    private void applyRequest(Property property, PropertyRequest request) {
        property.setTitle(request.title());
        property.setDescription(request.description());
        property.setType(request.type());
        property.setOperation(request.operation());
        property.setSalePrice(request.salePrice());
        property.setRentPrice(request.rentPrice());
        property.setCurrency(request.currency());
        property.setAddress(request.address());
        property.setZone(request.zone());
        property.setLat(request.lat());
        property.setLng(request.lng());

        boolean isResidential = request.type() == PropertyType.CASA || request.type() == PropertyType.DEPARTAMENTO;
        boolean isLand = request.type() == PropertyType.TERRENO;

        // Solo completamos los campos del tipo que corresponde; el resto
        // queda en null, tal como lo exige el CHECK de la base.
        property.setRooms(isResidential ? request.rooms() : null);
        property.setBedrooms(isResidential ? request.bedrooms() : null);
        property.setBathrooms(isResidential ? request.bathrooms() : null);
        property.setHasGarage(isResidential ? request.hasGarage() : null);
        property.setHasPatio(isResidential ? request.hasPatio() : null);

        property.setSurface(isLand ? request.surface() : null);
        property.setTerrainType(isLand ? request.terrainType() : null);
    }
}