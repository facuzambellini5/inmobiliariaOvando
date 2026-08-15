package com.example.InmobiliariaOvando.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, String fieldName, Object fieldValue) {
        super(entityName + " not found with " + fieldName + " : " + fieldValue);
    }
}
