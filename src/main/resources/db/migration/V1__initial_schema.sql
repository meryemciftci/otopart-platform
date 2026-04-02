-- ============================================================
-- OtoPart Platform  –  V1 Initial Schema
-- ============================================================

CREATE TABLE users (
    id                       BIGSERIAL PRIMARY KEY,
    email                    VARCHAR(255) NOT NULL UNIQUE,
    password                 VARCHAR(255) NOT NULL,
    first_name               VARCHAR(100) NOT NULL,
    last_name                VARCHAR(100) NOT NULL,
    phone                    VARCHAR(20)  UNIQUE,
    role                     VARCHAR(30)  NOT NULL DEFAULT 'CUSTOMER',
    city                     VARCHAR(50),
    district                 VARCHAR(100),
    full_address             TEXT,
    latitude                 DOUBLE PRECISION,
    longitude                DOUBLE PRECISION,
    loyalty_points           INT NOT NULL DEFAULT 0,
    total_order_count        INT NOT NULL DEFAULT 0,
    -- B2B / Usta
    tax_number               VARCHAR(20),
    company_name             VARCHAR(200),
    dbs_enabled              BOOLEAN DEFAULT FALSE,
    dbs_account_no           VARCHAR(50),
    -- Auth
    email_verified           BOOLEAN NOT NULL DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    refresh_token            TEXT,
    -- Audit
    active                   BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at               TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by               VARCHAR(255),
    updated_by               VARCHAR(255)
);

CREATE TABLE vehicles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    chassis_number  VARCHAR(17) NOT NULL,
    brand           VARCHAR(100) NOT NULL,
    model           VARCHAR(100) NOT NULL,
    year            INT NOT NULL,
    engine_code     VARCHAR(50),
    fuel_type       VARCHAR(30),
    transmission    VARCHAR(30),
    tire_size_front VARCHAR(20),
    tire_size_rear  VARCHAR(20),
    nickname        VARCHAR(100),
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);

CREATE TABLE suppliers (
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(200) NOT NULL,
    code              VARCHAR(50)  NOT NULL UNIQUE,
    warehouse_city    VARCHAR(50)  NOT NULL,
    warehouse_address TEXT,
    api_url           VARCHAR(500),
    api_key           VARCHAR(255),
    api_secret        VARCHAR(255),
    integration_type  VARCHAR(20) DEFAULT 'REST',
    ankara_warehouse  BOOLEAN NOT NULL DEFAULT FALSE,
    order_cutoff_time VARCHAR(5),  -- "16:00"
    contact_email     VARCHAR(255),
    contact_phone     VARCHAR(20),
    commission_rate   NUMERIC(5,2),
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255)
);

CREATE TABLE categories (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(200) NOT NULL,
    slug             VARCHAR(200) UNIQUE,
    description      TEXT,
    icon_url         VARCHAR(500),
    image_url        VARCHAR(500),
    parent_id        BIGINT REFERENCES categories(id),
    sort_order       INT NOT NULL DEFAULT 0,
    special_delivery BOOLEAN NOT NULL DEFAULT FALSE,  -- kaporta vb.
    express_category BOOLEAN NOT NULL DEFAULT FALSE,  -- yağ/bakım
    active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

CREATE TABLE products (
    id                    BIGSERIAL PRIMARY KEY,
    name                  VARCHAR(300) NOT NULL,
    sku                   VARCHAR(100) NOT NULL UNIQUE,
    barcode               VARCHAR(50),
    description           TEXT,
    category_id           BIGINT REFERENCES categories(id),
    supplier_id           BIGINT REFERENCES suppliers(id),
    price                 NUMERIC(10,2) NOT NULL,
    discounted_price      NUMERIC(10,2),
    stock                 INT NOT NULL DEFAULT 0,
    delivery_type         VARCHAR(30),
    tire_size             VARCHAR(20),
    requires_large_vehicle BOOLEAN NOT NULL DEFAULT FALSE,
    express_available     BOOLEAN NOT NULL DEFAULT FALSE,
    slug                  VARCHAR(300) UNIQUE,
    image_url             VARCHAR(500),
    supplier_product_code VARCHAR(100),
    active                BOOLEAN NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255)
);

CREATE TABLE product_labels (
    product_id BIGINT NOT NULL REFERENCES products(id),
    label      VARCHAR(50) NOT NULL,
    PRIMARY KEY (product_id, label)
);

CREATE TABLE product_oem_numbers (
    product_id BIGINT NOT NULL REFERENCES products(id),
    oem_number VARCHAR(100) NOT NULL,
    PRIMARY KEY (product_id, oem_number)
);

CREATE TABLE vehicle_compatibility (
    id             BIGSERIAL PRIMARY KEY,
    product_id     BIGINT NOT NULL REFERENCES products(id),
    brand          VARCHAR(100) NOT NULL,
    model          VARCHAR(100) NOT NULL,
    year_from      INT,
    year_to        INT,
    engine_code    VARCHAR(50),
    fuel_type      VARCHAR(30),
    transmission   VARCHAR(30),
    chassis_prefix VARCHAR(10)
);

CREATE TABLE coupons (
    id                 BIGSERIAL PRIMARY KEY,
    code               VARCHAR(50) NOT NULL UNIQUE,
    description        VARCHAR(300),
    type               VARCHAR(30) NOT NULL,
    discount_amount    NUMERIC(10,2),
    discount_percent   DOUBLE PRECISION,
    min_order_amount   NUMERIC(10,2),
    max_discount_amount NUMERIC(10,2),
    valid_from         TIMESTAMP,
    valid_until        TIMESTAMP,
    total_usage_limit  INT,
    used_count         INT NOT NULL DEFAULT 0,
    per_user_limit     INT,
    first_order_only   BOOLEAN NOT NULL DEFAULT FALSE,
    specific_user_id   BIGINT REFERENCES users(id),
    free_shipping      BOOLEAN NOT NULL DEFAULT FALSE,
    active             BOOLEAN NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255)
);

CREATE TABLE orders (
    id                   BIGSERIAL PRIMARY KEY,
    order_number         VARCHAR(30) NOT NULL UNIQUE,
    user_id              BIGINT NOT NULL REFERENCES users(id),
    vehicle_id           BIGINT REFERENCES vehicles(id),
    status               VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    subtotal             NUMERIC(10,2) NOT NULL,
    shipping_fee         NUMERIC(10,2) NOT NULL DEFAULT 0,
    discount_amount      NUMERIC(10,2) NOT NULL DEFAULT 0,
    total_amount         NUMERIC(10,2) NOT NULL,
    b2b_discount_applied BOOLEAN NOT NULL DEFAULT FALSE,
    b2b_discount_amount  NUMERIC(10,2) NOT NULL DEFAULT 0,
    free_shipping        BOOLEAN NOT NULL DEFAULT FALSE,
    free_shipping_reason VARCHAR(200),
    delivery_type        VARCHAR(30),
    delivery_date        DATE,
    delivery_slot        TIME,
    delivery_address     TEXT,
    delivery_city        VARCHAR(50),
    delivery_district    VARCHAR(100),
    coupon_code          VARCHAR(50),
    points_earned        INT NOT NULL DEFAULT 0,
    points_used          INT NOT NULL DEFAULT 0,
    payment_method       VARCHAR(30),
    payment_reference    VARCHAR(100),
    installment_count    INT,
    customer_note        TEXT,
    supplier_order_ref   VARCHAR(100),
    active               BOOLEAN NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255)
);

CREATE TABLE order_items (
    id                BIGSERIAL PRIMARY KEY,
    order_id          BIGINT NOT NULL REFERENCES orders(id),
    product_id        BIGINT NOT NULL REFERENCES products(id),
    quantity          INT NOT NULL,
    unit_price        NUMERIC(10,2) NOT NULL,
    total_price       NUMERIC(10,2) NOT NULL,
    sent_to_supplier  BOOLEAN NOT NULL DEFAULT FALSE,
    supplier_item_ref VARCHAR(100)
);

CREATE TABLE deliveries (
    id                    BIGSERIAL PRIMARY KEY,
    order_id              BIGINT NOT NULL UNIQUE REFERENCES orders(id),
    courier_id            BIGINT REFERENCES users(id),
    delivery_type         VARCHAR(30) NOT NULL,
    status                VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    scheduled_date        DATE,
    scheduled_slot        TIME,
    picked_up_at          TIMESTAMP,
    delivered_at          TIMESTAMP,
    cargo_company         VARCHAR(100),
    tracking_number       VARCHAR(100),
    courier_latitude      DOUBLE PRECISION,
    courier_longitude     DOUBLE PRECISION,
    location_updated_at   TIMESTAMP,
    address               TEXT,
    city                  VARCHAR(50),
    district              VARCHAR(100),
    requires_large_vehicle BOOLEAN NOT NULL DEFAULT FALSE,
    active                BOOLEAN NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255)
);

-- ── İndeksler ─────────────────────────────────────────────────────────────
CREATE INDEX idx_vehicles_user     ON vehicles(user_id);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_supplier ON products(supplier_id);
CREATE INDEX idx_products_sku      ON products(sku);
CREATE INDEX idx_products_tire     ON products(tire_size);
CREATE INDEX idx_compat_brand_model ON vehicle_compatibility(brand, model, year_from, year_to);
CREATE INDEX idx_orders_user       ON orders(user_id);
CREATE INDEX idx_orders_status     ON orders(status);
CREATE INDEX idx_deliveries_courier ON deliveries(courier_id);
