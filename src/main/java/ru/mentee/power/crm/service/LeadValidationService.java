package ru.mentee.power.crm.service;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.client.EmailValidationClient;
import ru.mentee.power.crm.spring.client.EmailValidationResponse;

/**
 * Сервис создания лидов с внешней валидацией email и retry/fallback (Variant A).
 *
 * <p>Использует существующий {@link LeadService} и {@link EmailValidationClient}.
 */
@Service
public class LeadValidationService {

  private final LeadService leadService;
  private final EmailValidationClient emailValidationClient;

  public LeadValidationService(
      LeadService leadService, EmailValidationClient emailValidationClient) {
    this.leadService = leadService;
    this.emailValidationClient = emailValidationClient;
  }

  /**
   * Создаёт лида с предварительной проверкой email во внешнем сервисе.
   *
   * <p>При ошибках или невалидном email метод выбрасывает исключение и будет повторён в
   * соответствии с конфигурацией resilience4j.retry.instances.email-validation.
   */
  @Retry(name = "email-validation", fallbackMethod = "createLeadFallback")
  public Lead createLeadWithValidation(
      String email, String company, LeadStatus status, Address address, String phone) {
    EmailValidationResponse response = emailValidationClient.validateEmail(email);
    if (!response.valid()) {
      throw new IllegalArgumentException("Invalid email: " + response.reason());
    }
    return leadService.addLead(email, company, status, address, phone);
  }

  /**
   * Fallback вызывается, если после всех попыток retry метод {@link
   * #createLeadWithValidation(String, String, LeadStatus, Address, String)} так и не выполнился
   * успешно (например, внешнее API недоступно).
   *
   * <p>Для упрощения Variant A: создаём лида без валидации, чтобы не блокировать бизнес-поток.
   */
  @SuppressWarnings("unused")
  public Lead createLeadFallback(
      String email,
      String company,
      LeadStatus status,
      Address address,
      String phone,
      Throwable throwable) {
    return leadService.addLead(email, company, status, address, phone);
  }
}
