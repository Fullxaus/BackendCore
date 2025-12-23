package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LeadTest {

    @Test
    void shouldReturnId_whenGetIdCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Lead lead = new Lead(id, "email", "phone", "company", "status");

        // When
        UUID result = lead.id();

        // Then
        assertThat(result).isEqualTo(id);
    }

    @Test
    void shouldReturnEmail_whenGetEmailCalled() {
        // Given
        String email = "test@example.com";
        Lead lead = new Lead(UUID.randomUUID(), email, "phone", "company", "status");

        // When
        String result = lead.email();

        // Then
        assertThat(result).isEqualTo(email);
    }

    @Test
    void shouldReturnPhone_whenGetPhoneCalled() {
        // Given
        String phone = "+1234567890";
        Lead lead = new Lead(UUID.randomUUID(), "email", phone, "company", "status");

        // When
        String result = lead.phone();

        // Then
        assertThat(result).isEqualTo(phone);
    }

    @Test
    void shouldReturnCompany_whenGetCompanyCalled() {
        // Given
        String company = "Test Company";
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", company, "status");

        // When
        String result = lead.company();

        // Then
        assertThat(result).isEqualTo(company);
    }

    @Test
    void shouldReturnStatus_whenGetStatusCalled() {
        // Given
        String status = "New";
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", status);

        // When
        String result = lead.status();

        // Then
        assertThat(result).isEqualTo(status);
    }

    @Test
    void shouldReturnFormattedString_whenToStringCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Lead lead = new Lead(id, "test@example.com", "+1234567890", "Test Company", "New");

        // When
        String result = lead.toString();

        // Then
        assertThat(result).contains(String.valueOf(id));
        assertThat(result).contains("test@example.com");
        assertThat(result).contains("+1234567890");
        assertThat(result).contains("Test Company");
        assertThat(result).contains("New");
    }
}
