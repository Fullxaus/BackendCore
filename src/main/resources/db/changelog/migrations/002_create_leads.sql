--liquibase formatted sql
--changeset your-name:BCORE-32-2

CREATE TABLE leads (
    id UUID PRIMARY KEY NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50) NOT NULL,
    company VARCHAR(255) NOT NULL,
    company_id UUID REFERENCES companies(id),
    status VARCHAR(50) NOT NULL,
    city VARCHAR(100),
    street VARCHAR(255),
    zip VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);
