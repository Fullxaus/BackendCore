package ru.mentee.power.crm.spring.rest.fixed.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO запроса на создание приглашённого.
 *
 * @see ru.mentee.power.crm.spring.rest.fixed.InviteeController
 */
public record CreateInviteeRequest(
    @NotBlank(message = "Email обязателен")
        @Email(message = "Email должен быть в корректном формате")
        @Size(max = 255)
        String email,
    @NotBlank(message = "Имя обязательно")
        @Size(min = 2, max = 50, message = "Имя от 2 до 50 символов")
        String firstName) {}
