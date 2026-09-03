package com.example.InmobiliariaOvando.services;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.InmobiliariaOvando.exceptions.BusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService {

    //Ancho máximo de la imágen -> se ve bien y no es pesado
    private static final int MAX_WIDTH_PX = 1920;

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public UploadedImage upload(MultipartFile file, UUID propertyId) {
        try {
            // "incoming transformation": se aplica ANTES de guardar, así
            // que lo que Cloudinary termina almacenando ya es la versión
            // chica, no el original pesado.
            //
            // - width + crop("limit"): si la foto mide más de 1920px de
            //   ancho, la achica. Si ya es más chica, la deja igual
            //   (nunca la agranda ni la deforma).
            // - quality("auto:good"): Cloudinary elige automáticamente
            //   cuánto puede comprimir sin que se note la pérdida de
            //   calidad. Solo esto ya suele bajar el peso a la mitad.
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "inmobiliaria-ovando/properties/" + propertyId,
                    "resource_type", "image",
                    "transformation", new Transformation()
                            .width(MAX_WIDTH_PX)
                            .crop("limit")
                            .quality("auto:good")
            ));

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            return new UploadedImage(url, publicId);
        } catch (IOException e) {
            throw new BusinessRuleException("No se pudo subir la imagen, probá de nuevo en unos segundos");
        }
    }

    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new BusinessRuleException("No se pudo eliminar la imagen de Cloudinary, probá de nuevo");
        }
    }

    public record UploadedImage(String url, String publicId) {
    }
}