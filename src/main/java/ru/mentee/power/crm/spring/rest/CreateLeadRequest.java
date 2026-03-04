package ru.mentee.power.crm.spring.rest;

/**
 * DTO для создания лида через REST API. Поддерживает Postman: email, firstName, lastName, status.
 * Если company не указан, используется firstName + " " + lastName.
 */

public record CreateLeadRequest(
    String email,
    String firstName,
    String lastName,
    String company,
    String status,
    String phone,
    String city,
    String street,
    String zip) {}
