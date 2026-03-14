package ru.mentee.power.crm.spring.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Централизованный обработчик всех исключений в REST API. Перехватывает ошибки из
 * всех @RestController классов.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Обрабатывает ошибки Bean Validation (@Valid).
   *
   * <p>Переопределяем метод из ResponseEntityExceptionHandler для кастомного форматирования field
   * errors.
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            fe ->
                errors.put(
                    fe.getField(), fe.getDefaultMessage() != null ? fe.getDefaultMessage() : ""));
    String path =
        request.getDescription(false).startsWith("uri=")
            ? request.getDescription(false).substring(4)
            : request.getDescription(false);
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Validation failed",
            path,
            errors);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  /** Обрабатывает ConstraintViolationException (@PathVariable / @RequestParam с @Validated). */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations()
        .forEach(
            v ->
                errors.put(
                    v.getPropertyPath().toString(), v.getMessage() != null ? v.getMessage() : ""));
    String path =
        request.getDescription(false).startsWith("uri=")
            ? request.getDescription(false).substring(4)
            : request.getDescription(false);
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Validation failed",
            path,
            errors);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  /** Обрабатывает EntityNotFoundException (404 Not Found). */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFound(
      EntityNotFoundException ex, WebRequest request) {
    String path =
        request.getDescription(false).startsWith("uri=")
            ? request.getDescription(false).substring(4)
            : request.getDescription(false);
    ErrorResponse errorResponse =
        new ErrorResponse(LocalDateTime.now(), 404, "Not Found", ex.getMessage(), path);
    log.warn("Entity not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /** Обрабатывает DuplicateEmailException (409 Conflict). */
  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmail(
      DuplicateEmailException ex, WebRequest request) {
    String path =
        request.getDescription(false).startsWith("uri=")
            ? request.getDescription(false).substring(4)
            : request.getDescription(false);
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), path);
    log.warn("Duplicate email: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  /** Обрабатывает EmailAlreadyExistsException (409 Conflict). */
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
      EmailAlreadyExistsException ex, WebRequest request) {
    String path =
        request.getDescription(false).startsWith("uri=")
            ? request.getDescription(false).substring(4)
            : request.getDescription(false);
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(), HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), path);
    log.warn("Email already exists: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  /** Обрабатывает InvalidStatusException (400 Bad Request). */
  @ExceptionHandler(InvalidStatusException.class)
  public ResponseEntity<ErrorResponse> handleInvalidStatus(
      InvalidStatusException ex, WebRequest request) {
    String path =
        request.getDescription(false).startsWith("uri=")
            ? request.getDescription(false).substring(4)
            : request.getDescription(false);
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            path);
    log.warn("Invalid status: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Fallback обработчик для всех непредвиденных исключений (500 Internal Server Error).
   *
   * <p>НЕ показываем stack trace клиенту, но логируем на сервере.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
    log.error("Unexpected error", ex);
    String path =
        request.getDescription(false).startsWith("uri=")
            ? request.getDescription(false).substring(4)
            : request.getDescription(false);
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Internal server error occurred",
            path);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
