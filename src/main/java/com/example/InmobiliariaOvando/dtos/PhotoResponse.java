package com.example.InmobiliariaOvando.dtos;

import java.util.UUID;

import com.example.InmobiliariaOvando.models.Photo;

public record PhotoResponse(UUID id, String url, Short position) {
    public PhotoResponse(Photo photo) {
        this(photo.getId(), photo.getUrl(), photo.getPosition());
    }
}