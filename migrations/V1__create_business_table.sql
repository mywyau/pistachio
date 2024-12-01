DROP TABLE IF EXISTS business_address;

CREATE TABLE business_address (
    id BIGSERIAL PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL UNIQUE,
    business_name VARCHAR(255),
    address_1 VARCHAR(255),
    address_2 VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    county VARCHAR(255),
    postcode VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS business_details;

CREATE TABLE business_contact_details (
    id BIGSERIAL PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL UNIQUE,
    business_name VARCHAR(255),
    primary_contact VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    website_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS business_listing;

CREATE TABLE business_specs (
    id SERIAL PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL UNIQUE,
    business_name VARCHAR(255) NOT NULL,
    description TEXT,
    business_type VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

