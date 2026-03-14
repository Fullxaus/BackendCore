package ru.mentee.power.crm.spring.exception;

/**
 * Исключение при попытке создать лид с email, который уже существует. Маппится на HTTP 409
 * Conflict.
 */
public class DuplicateEmailException extends BusinessException {

  public DuplicateEmailException(String email) {
    super("Lead with email already exists: " + email);
  }
}
