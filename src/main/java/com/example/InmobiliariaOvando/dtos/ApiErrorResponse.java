package com.example.InmobiliariaOvando.dtos;

import java.time.Instant;


public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp
) {
}