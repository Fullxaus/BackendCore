package ru.mentee.power.crm.spring.rest.fixed.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * @see ru.mentee.power.crm.spring.rest.fixed.InviteeController
 */
public record InviteeResponse(UUID id, String email, String firstName, String status, Instant createdAt) {}
