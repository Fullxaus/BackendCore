--liquibase formatted sql
--changeset your-name:BCORE-32-6
--comment: Добавляет company_id в leads для совместимости с LeadEntity @ManyToOne Company (если колонки ещё нет)

ALTER TABLE leads ADD COLUMN IF NOT EXISTS company_id UUID REFERENCES companies(id);
