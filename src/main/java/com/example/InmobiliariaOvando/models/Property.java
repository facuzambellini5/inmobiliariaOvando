package com.example.InmobiliariaOvando.models;

import com.example.InmobiliariaOvando.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PropertyType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OperationType operation;

    @Column(precision = 14, scale = 2)
    private BigDecimal salePrice;

    @Column(precision = 14, scale = 2)
    private BigDecimal rentPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency = Currency.USD;

    //Address
    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    private Zone zone;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    //House details
    private Short rooms;
    private Short bedrooms;
    private Short bathrooms;
    private Boolean hasGarage;
    private Boolean hasPatio;

    //Lot details
    private BigDecimal surface;
    @Enumerated(EnumType.STRING)
    private TerrainType terrainType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PropertyStatus status = PropertyStatus.DISPONIBLE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    // mappedBy = "property": la FK vive del lado de PropertyPhoto, esta
    // lista es solo la "vista" desde Property. orphanRemoval = true: si
    // sacás una foto de esta lista y guardás, se borra de la base sola.
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Photo> photos = new ArrayList<>();
}