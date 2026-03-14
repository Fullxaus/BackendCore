package ru.mentee.power.crm.spring.exception;

/**
 * Базовое исключение для всех бизнес-ошибок в приложении. Все кастомные исключения наследуются от
 * этого класса.
 */
public abstract class BusinessException extends RuntimeException {

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }
}
