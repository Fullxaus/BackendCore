package ru.mentee.power.crm.domain;

import java.util.Objects;
import java.util.UUID;

public record Lead(
        UUID id,
        Contact contact,
        String company,
        String status
) {
    // Компактный конструктор с валидацией
    public Lead {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }
        if (contact == null) {
            throw new IllegalArgumentException("Контакт не может быть null");
        }
        if (status == null || !isValidStatus(status)) {
            throw new IllegalArgumentException("Недопустимый статус");
        }
    }

    // Валидация статуса
    private boolean isValidStatus(String status) {
        return "NEW".equals(status) ||
                "QUALIFIED".equals(status) ||
                "CONVERTED".equals(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lead lead = (Lead) o;
        return id.equals(lead.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
