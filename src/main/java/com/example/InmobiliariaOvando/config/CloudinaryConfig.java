package com.example.InmobiliariaOvando.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// El objeto Cloudinary es el "cliente HTTP" que sabe hablar con la API
// de Cloudinary. Se arma una sola vez acá, como un @Bean, para que
// Spring lo inyecte donde haga falta (en vez de crear uno nuevo cada vez)
@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(@Value("${cloudinary.cloud-name}") String cloudName,
                                 @Value("${cloudinary.api-key}") String apiKey,
                                 @Value("${cloudinary.api-secret}") String apiSecret) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}