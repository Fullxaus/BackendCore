--liquibase formatted sql
--changeset your-name:BCORE-32-4

CREATE TABLE deals (
    id UUID PRIMARY KEY NOT NULL,
    lead_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
