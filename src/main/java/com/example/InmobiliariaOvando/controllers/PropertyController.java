package com.example.InmobiliariaOvando.controllers;

import com.example.InmobiliariaOvando.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {

    @Autowired
    private PropertyService propertyService;


}
