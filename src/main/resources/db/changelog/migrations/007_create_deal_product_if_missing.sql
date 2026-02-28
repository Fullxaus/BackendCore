--liquibase formatted sql
--changeset your-name:BCORE-33-2
--comment: Создание deal_product если таблица отсутствует (например, 006 была пропущена)

CREATE TABLE IF NOT EXISTS deal_product (
    id UUID PRIMARY KEY NOT NULL,
    deal_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    CONSTRAINT fk_deal_product_deal FOREIGN KEY (deal_id) REFERENCES deals(id) ON DELETE CASCADE,
    CONSTRAINT fk_deal_product_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT uq_deal_product_deal_product UNIQUE (deal_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_deal_product_deal_id ON deal_product(deal_id);
CREATE INDEX IF NOT EXISTS idx_deal_product_product_id ON deal_product(product_id);
