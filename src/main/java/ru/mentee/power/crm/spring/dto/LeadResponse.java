package ru.mentee.power.crm.spring.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO-ответ для REST API Lead.
 *
 * <p>Иммутабельный Java record: автоматически генерирует equals/hashCode/toString.
 */
public record LeadResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    String company,
    LocalDateTime createdAt) {}
