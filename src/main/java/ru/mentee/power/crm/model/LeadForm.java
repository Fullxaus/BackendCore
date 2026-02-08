package ru.mentee.power.crm.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO формы создания/редактирования лида для Bean Validation.
 * Поля name, email, phone, status биндятся с формы; при сохранении name используется как company.
 */
public record LeadForm(
        @NotBlank(message = "Имя обязательно")
        String name,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email,

        @NotBlank(message = "Телефон обязателен")
        String phone,

        @NotNull(message = "Статус обязателен")
        LeadStatus status
) {
    /** Пустая форма для GET /leads/new */
    public LeadForm() {
        this("", "", "", null);
    }

    public LeadForm {
        name = name != null ? name : "";
        email = email != null ? email : "";
        phone = phone != null ? phone : "";
    }
}
