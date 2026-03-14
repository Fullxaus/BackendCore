package ru.mentee.power.crm.spring.exception;

/**
 * Исключение при попытке создать ресурс с email, который уже существует. Маппится на HTTP 409
 * Conflict.
 */
public class EmailAlreadyExistsException extends BusinessException {

  public EmailAlreadyExistsException(String email) {
    super("Email already exists: " + email);
  }
}
