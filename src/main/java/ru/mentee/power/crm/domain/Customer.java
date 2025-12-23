package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Customer(
        UUID id,
        Contact contact,
        Address billingAddress,
        String loyaltyTier
) {
    // Компактный конструктор с валидацией
    public Customer {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }
        if (contact == null) {
            throw new IllegalArgumentException("Контакт не может быть null");
        }
        if (billingAddress == null) {
            throw new IllegalArgumentException("Платёжный адрес не может быть null");
        }
        if (loyaltyTier == null || !isValidLoyaltyTier(loyaltyTier)) {
            throw new IllegalArgumentException("Недопустимый уровень лояльности");
        }
    }

    // Валидация уровня лояльности
    private boolean isValidLoyaltyTier(String loyaltyTier) {
        return "BRONZE".equals(loyaltyTier) ||
                "SILVER".equals(loyaltyTier) ||
                "GOLD".equals(loyaltyTier);
    }
}
