package ru.mentee.power.crm.spring.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class EmailValidationClient {

  private static final Logger log = LoggerFactory.getLogger(EmailValidationClient.class);

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public EmailValidationClient(
      RestTemplate restTemplate,
      @Value("${email.validation.base-url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public EmailValidationResponse validateEmail(String email) {
    String url = baseUrl + "/api/validate/email?email=" + email;
    try {
      EmailValidationResponse response =
          restTemplate.getForObject(url, EmailValidationResponse.class);
      if (response == null) {
        return new EmailValidationResponse(email, false, "Empty response from validation service");
      }
      return response;
    } catch (RestClientException ex) {
      log.error("Error calling email validation service: {}", ex.getMessage(), ex);
      return new EmailValidationResponse(
          email, false, "Validation service error: " + ex.getMessage());
    }
  }
}
