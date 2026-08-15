-- ============================================
-- PROPERTIES
-- ============================================
CREATE TABLE properties
(
    id           UUID PRIMARY KEY          DEFAULT gen_random_uuid(),
    title        TEXT             NOT NULL,
    description  TEXT             NOT NULL,
    type         VARCHAR(20)      NOT NULL,
    operation    VARCHAR(20)      NOT NULL,
    sale_price   NUMERIC(14, 2),
    rent_price   NUMERIC(14, 2),
    currency     VARCHAR(3)       NOT NULL,
    address      TEXT             NOT NULL,
    zone         VARCHAR(20),
    lat          DOUBLE PRECISION NOT NULL,
    lng          DOUBLE PRECISION NOT NULL,
    status       VARCHAR(20)      NOT NULL DEFAULT 'DISPONIBLE',

    -- Casa / Departamento
    ambientes    SMALLINT,
    bedrooms     SMALLINT,
    bathrooms    SMALLINT,
    garage       BOOLEAN,
    patio        BOOLEAN,

    -- Terreno
    surface      NUMERIC(12, 2),
    terrain_type VARCHAR(20),

    created_at   TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ      NOT NULL DEFAULT now(),

    -- Listas de valores permitidos (reemplazan a los ENUMs nativos).
    -- En mayúsculas para que coincidan 1 a 1 con el .name() de los enums
    -- de Java: así @Enumerated(EnumType.STRING) funciona directo, sin
    -- necesitar un converter que traduzca entre los dos formatos.
    CONSTRAINT chk_type CHECK (type IN ('CASA', 'DEPARTAMENTO', 'TERRENO', 'LOCAL_COMERCIAL', 'CAMPO', 'COCHERA')),
    CONSTRAINT chk_operation CHECK (operation IN ('VENTA', 'ALQUILER', 'AMBAS', 'INFORMATIVA')),
    CONSTRAINT chk_currency CHECK (currency IN ('ARS', 'USD')),
    CONSTRAINT chk_status CHECK (status IN ('DISPONIBLE', 'ALQUILADA', 'VENDIDA')),
    CONSTRAINT chk_terrain_type CHECK (terrain_type IS NULL OR terrain_type IN ('RESIDENCIAL', 'COMERCIAL')),
    CONSTRAINT chk_zone_values CHECK (zone IS NULL OR zone IN ('CENTRO', 'ENSANCHE')),

    -- Reglas de negocio (igual que el prototipo de Lovable)
    CONSTRAINT chk_lat CHECK (lat BETWEEN -90 AND 90),
    CONSTRAINT chk_lng CHECK (lng BETWEEN -180 AND 180),

    -- Cochera: solo informativa, sin precios
    CONSTRAINT chk_cochera CHECK (
        type <> 'COCHERA'
            OR (operation = 'INFORMATIVA' AND sale_price IS NULL AND rent_price IS NULL)
        ),
    -- Informativa solo aplica a Cochera
    CONSTRAINT chk_informativa CHECK (operation <> 'INFORMATIVA' OR type = 'COCHERA'),
    -- Campo: sin zona
    CONSTRAINT chk_campo_zone CHECK (type <> 'CAMPO' OR zone IS NULL),
    -- Zona obligatoria para el resto
    CONSTRAINT chk_zone_required CHECK (type = 'CAMPO' OR zone IS NOT NULL),
    -- Precios según operación
    CONSTRAINT chk_sale_price CHECK (operation NOT IN ('VENTA', 'AMBAS') OR sale_price IS NOT NULL),
    CONSTRAINT chk_rent_price CHECK (operation NOT IN ('ALQUILER', 'AMBAS') OR rent_price IS NOT NULL),
    -- Campos de vivienda solo en Casa/Departamento
    CONSTRAINT chk_house_fields CHECK (
        type IN ('CASA', 'DEPARTAMENTO')
            OR (ambientes IS NULL AND bedrooms IS NULL AND bathrooms IS NULL AND garage IS NULL AND patio IS NULL)
        ),
    -- Campos de terreno solo en Terreno
    CONSTRAINT chk_land_fields CHECK (
        type = 'TERRENO'
            OR (surface IS NULL AND terrain_type IS NULL)
        ),
    CONSTRAINT chk_land_required CHECK (
        type <> 'TERRENO' OR (surface IS NOT NULL AND terrain_type IS NOT NULL)
        )
);

CREATE INDEX idx_properties_status ON properties (status);
CREATE INDEX idx_properties_type ON properties (type);
CREATE INDEX idx_properties_operation ON properties (operation);
CREATE INDEX idx_properties_zone ON properties (zone);
CREATE INDEX idx_properties_created_at ON properties (created_at DESC);

-- ============================================
-- PROPERTY PHOTOS (0..14 por propiedad, ordenadas)
-- ============================================
CREATE TABLE property_photos
(
    id          UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    property_id UUID        NOT NULL REFERENCES properties (id) ON DELETE CASCADE,
    url         TEXT        NOT NULL,
    position    SMALLINT    NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_photo_position UNIQUE (property_id, position),
    CONSTRAINT chk_position_range CHECK (position BETWEEN 0 AND 14)
);

CREATE INDEX idx_photos_property ON property_photos (property_id, position);

-- ============================================
-- ADMIN USERS
-- ============================================
-- id como BIGINT en vez de UUID: nunca se expone en una URL pública
-- (solo se usa internamente para el login del panel admin), así que
-- no hace falta pagar el costo extra de un UUID acá.
--
-- enabled / account_non_locked: estos dos existen específicamente porque
-- Spring Security los pide en la interfaz UserDetails (isEnabled() /
-- isAccountNonLocked()). Con esto podés, por ejemplo, desactivar el acceso
-- de alguien sin borrar su usuario, o bloquearlo después de X intentos
-- fallidos de login sin tocar el resto de sus datos.
CREATE TABLE admin_users
(
    id                 UUID PRIMARY KEY          DEFAULT gen_random_uuid(),
    username           TEXT        NOT NULL UNIQUE,
    password_hash      TEXT        NOT NULL,
    enabled            BOOLEAN     NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_login_at      TIMESTAMPTZ
);