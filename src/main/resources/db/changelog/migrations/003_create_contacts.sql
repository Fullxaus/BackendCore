--liquibase formatted sql
--changeset your-name:BCORE-32-3

CREATE TABLE contacts (
    id UUID PRIMARY KEY NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    city VARCHAR(100),
    street VARCHAR(255),
    zip VARCHAR(20)
);
