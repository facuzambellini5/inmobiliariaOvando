ALTER TABLE property_photos
    ADD COLUMN public_id TEXT NOT NULL,
    ADD CONSTRAINT uq_property_photos_public_id UNIQUE (public_id);