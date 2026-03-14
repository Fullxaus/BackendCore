package ru.mentee.power.crm.spring.rest.fixed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO запроса на обновление статуса.
 *
 * @see ru.mentee.power.crm.spring.rest.fixed.InviteeController
 */
public record UpdateInviteeStatusRequest(
    @NotBlank(message = "Статус обязателен")
        @Pattern(regexp = "ACTIVE|INACTIVE", message = "Статус должен быть ACTIVE или INACTIVE")
        String status) {}
