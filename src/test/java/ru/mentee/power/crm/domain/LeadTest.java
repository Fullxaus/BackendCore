package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LeadTest {

    @Test
    void shouldReturnId_whenGetIdCalled() {
        // Given
        String id = "123";
        Lead lead = new Lead(id, "email", "phone", "company", "status");

        // When
        String result = lead.getId();

        // Then
        assertThat(result).isEqualTo(id);
    }

    @Test
    void shouldReturnEmail_whenGetEmailCalled() {
        // Given
        String email = "test@example.com";
        Lead lead = new Lead("id", email, "phone", "company", "status");

        // When
        String result = lead.getEmail();

        // Then
        assertThat(result).isEqualTo(email);
    }

    @Test
    void shouldReturnPhone_whenGetPhoneCalled() {
        // Given
        String phone = "+1234567890";
        Lead lead = new Lead("id", "email", phone, "company", "status");

        // When
        String result = lead.getPhone();

        // Then
        assertThat(result).isEqualTo(phone);
    }

    @Test
    void shouldReturnCompany_whenGetCompanyCalled() {
        // Given
        String company = "Test Company";
        Lead lead = new Lead("id", "email", "phone", company, "status");

        // When
        String result = lead.getCompany();

        // Then
        assertThat(result).isEqualTo(company);
    }

    @Test
    void shouldReturnStatus_whenGetStatusCalled() {
        // Given
        String status = "New";
        Lead lead = new Lead("id", "email", "phone", "company", status);

        // When
        String result = lead.getStatus();

        // Then
        assertThat(result).isEqualTo(status);
    }

    @Test
    void shouldReturnFormattedString_whenToStringCalled() {
        // Given
        Lead lead = new Lead("123", "test@example.com", "+1234567890", "Test Company", "New");

        // When
        String result = lead.toString();

        // Then
        assertThat(result).isEqualTo("Lead{id='123', email='test@example.com', phone='+1234567890', company='Test Company', status='New'}");
    }
}

