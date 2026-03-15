package ru.mentee.power.crm.spring.exception;

/** Исключение при невалидном значении статуса. Маппится на HTTP 400 Bad Request. */
public class InvalidStatusException extends BusinessException {

  public InvalidStatusException(String message) {
    super(message);
  }
}
