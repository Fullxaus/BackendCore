package ru.mentee.power.crm.spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания лида через REST API.
 *
 * <p>Не содержит id и timestamps — они генерируются на стороне сервера/БД.
 */
public class CreateLeadRequest {

  @NotBlank(message = "Email обязателен")
  @Email(message = "Email должен быть в корректном формате")
  @Size(max = 255)
  private String email;

  @NotBlank(message = "Имя не может быть пустым")
  @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
  private String firstName;

  @NotBlank(message = "Фамилия обязательна")
  @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
  private String lastName;

  @Size(max = 100, message = "Название компании не должно превышать 100 символов")
  private String company;

  public CreateLeadRequest(String email, String firstName, String lastName, String company) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.company = company;
  }

  public CreateLeadRequest() {}

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }
}
