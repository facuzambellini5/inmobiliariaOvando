package com.example.InmobiliariaOvando.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "property_photos")
public class PropertyPhoto {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // FetchType.LAZY: cuando traés una foto, Hibernate NO va a buscar
    // automáticamente toda la propiedad dueña salvo que se lo pidas
    // explícitamente. Evita queries de más.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false)
    private Short position;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}