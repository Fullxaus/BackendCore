package ru.mentee.power.crm.domain;


import java.util.UUID;

public record Lead(
        UUID id,
        String email,
        String phone,
        String company,
        String status
) {
    // Кастомный конструктор с валидацией
    public Lead(UUID id, String email, String phone, String company, String status) {
        this.id = id;
        this.email = email.isEmpty() ? null : email;
        this.phone = phone.isEmpty() ? null : phone;
        this.company = company.isEmpty() ? null : company;
        this.status = status.isEmpty() ? null : status;

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Phone is required");
        }
        if (company == null || company.isEmpty()) {
            throw new IllegalArgumentException("Company is required");
        }
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
    }
}
