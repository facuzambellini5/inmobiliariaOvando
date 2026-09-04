package com.example.InmobiliariaOvando.services;

import java.util.UUID;

import com.example.InmobiliariaOvando.dtos.PropertyFilterRequest;
import com.example.InmobiliariaOvando.dtos.PropertyRequest;
import com.example.InmobiliariaOvando.dtos.PropertyResponse;
import com.example.InmobiliariaOvando.enums.PropertyStatus;
import com.example.InmobiliariaOvando.enums.PropertyType;
import com.example.InmobiliariaOvando.exceptions.EntityNotFoundException;
import com.example.InmobiliariaOvando.models.Property;
import com.example.InmobiliariaOvando.repositories.IPropertyRepository;
import com.example.InmobiliariaOvando.repositories.specifications.PropertySpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertyService {

    private final IPropertyRepository propertyRepo;
    private final PhotoService photoService;

    public PropertyService(IPropertyRepository propertyRepo,
                           PhotoService photoService) {
        this.photoService = photoService;
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

    // El filtro puede venir "vacío" (todos sus campos en null): el
    // listado sin filtros del admin sigue pasando por acá y se comporta
    // exactamente igual que antes, ya que PropertySpecifications ignora
    // los campos no provistos.
    @Transactional(readOnly = true)
    public Page<PropertyResponse> findAll(Pageable pageable, PropertyFilterRequest filter) {
        return propertyRepo
                .findAll(PropertySpecifications.build(filter), pageable)
                .map(PropertyResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PropertyResponse> findByStatus(PropertyStatus status,Pageable pageable) {
        return propertyRepo.findByStatus(status, pageable)
                .map(PropertyResponse::new);
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
        Property property = propertyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property", "id", id.toString()));

        photoService.deleteAllForProperty(property);
        propertyRepo.delete(property);
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

        property.setRooms(isResidential ? request.rooms() : null);
        property.setBedrooms(isResidential ? request.bedrooms() : null);
        property.setBathrooms(isResidential ? request.bathrooms() : null);
        property.setHasGarage(isResidential ? request.hasGarage() : null);
        property.setHasPatio(isResidential ? request.hasPatio() : null);

        property.setSurface(isLand ? request.surface() : null);
        property.setTerrainType(isLand ? request.terrainType() : null);

        property.setStatus(request.status());
    }
}