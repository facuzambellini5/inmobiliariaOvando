package com.example.InmobiliariaOvando.services;

import java.util.List;
import java.util.UUID;

import com.example.InmobiliariaOvando.dtos.PhotoResponse;
import com.example.InmobiliariaOvando.exceptions.BusinessRuleException;
import com.example.InmobiliariaOvando.exceptions.EntityNotFoundException;
import com.example.InmobiliariaOvando.models.Property;
import com.example.InmobiliariaOvando.models.Photo;
import com.example.InmobiliariaOvando.repositories.IPhotoRepository;
import com.example.InmobiliariaOvando.repositories.IPropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {

    private static final int MAX_PHOTOS_PER_PROPERTY = 15;
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private final IPropertyRepository propertyRepo;
    private final IPhotoRepository photoRepo;
    private final CloudinaryService cloudinaryService;

    public PhotoService(IPropertyRepository propertyRepo,
                        IPhotoRepository photoRepo,
                        CloudinaryService cloudinaryService) {
        this.propertyRepo = propertyRepo;
        this.photoRepo = photoRepo;
        this.cloudinaryService = cloudinaryService;
    }

    public List<PhotoResponse> findByPropertyId(UUID propertyId) {
        return photoRepo.findByPropertyIdOrderByPositionAsc(propertyId).stream()
                .map(PhotoResponse::new)
                .toList();
    }

    @Transactional
    public PhotoResponse upload(UUID propertyId, MultipartFile file) {
        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property", "id", propertyId.toString()));

        validateFile(file);

        int currentCount = property.getPhotos().size();
        if (currentCount >= MAX_PHOTOS_PER_PROPERTY) {
            throw new BusinessRuleException("Esta propiedad ya tiene el máximo de " + MAX_PHOTOS_PER_PROPERTY + " fotos");
        }

        // Subimos primero a Cloudinary y recién después guardamos en la
        // base. Si Cloudinary falla, no queda ningún registro apuntando
        // a una imagen que nunca se llegó a subir.
        CloudinaryService.UploadedImage uploaded = cloudinaryService.upload(file, propertyId);

        Photo photo = new Photo();
        photo.setProperty(property);
        photo.setUrl(uploaded.url());
        photo.setPublicId(uploaded.publicId());
        photo.setPosition((short) currentCount);

        return new PhotoResponse(photoRepo.save(photo));
    }

    @Transactional
    public void delete(UUID propertyId, UUID photoId) {
        Photo photo = photoRepo.findById(photoId)
                // Chequeamos que la foto sea de ESA propiedad. Sin esto,
                // alguien podría borrar una foto ajena adivinando el id.
                .filter(p -> p.getProperty().getId().equals(propertyId))
                .orElseThrow(() -> new EntityNotFoundException("PropertyPhoto", "id", photoId.toString()));

        cloudinaryService.delete(photo.getPublicId());
        photoRepo.delete(photo);

        reorderPositions(propertyId);
    }

    // Se usa al borrar la PROPIEDAD entera (ver PropertyService).
    public void deleteAllForProperty(Property property) {
        for (Photo photo : property.getPhotos()) {
            cloudinaryService.delete(photo.getPublicId());
        }
    }

    private void reorderPositions(UUID propertyId) {
        List<Photo> remaining = photoRepo.findByPropertyIdOrderByPositionAsc(propertyId);
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setPosition((short) i);
        }
        photoRepo.saveAll(remaining);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("El archivo está vacío");
        }

        String contentType = file.getContentType();

        // Si viene nulo, lo frenamos antes
        if (contentType == null) {
            throw new BusinessRuleException("No se pudo determinar el tipo de archivo");
        }

        // Pasar a minúsculas para evitar problemas de "IMAGE/PNG"
        if (!ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessRuleException("Solo se aceptan imágenes JPG, PNG o WEBP. Formato recibido: " + contentType);
        }
    }
}