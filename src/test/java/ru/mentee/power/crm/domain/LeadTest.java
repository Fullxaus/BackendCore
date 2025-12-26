package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.UUID;

public class LeadTest {

    @Test
    void shouldReturnId_whenGetIdCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Lead lead = new Lead(id, "email", "phone", "company", "status");

        // When
        UUID result = lead.getId();

        // Then
        assertThat(result).isEqualTo(id);
    }

    @Test
    void shouldReturnEmail_whenGetEmailCalled() {
        // Given
        String email = "test@example.com";
        Lead lead = new Lead(UUID.randomUUID(), email, "phone", "company", "status");

        // When
        String result = lead.getEmail();

        // Then
        assertThat(result).isEqualTo(email);
    }

    @Test
    void shouldReturnPhone_whenGetPhoneCalled() {
        // Given
        String phone = "+1234567890";
        Lead lead = new Lead(UUID.randomUUID(), "email", phone, "company", "status");

        // When
        String result = lead.getPhone();

        // Then
        assertThat(result).isEqualTo(phone);
    }

    @Test
    void shouldReturnCompany_whenGetCompanyCalled() {
        // Given
        String company = "Test Company";
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", company, "status");

        // When
        String result = lead.getCompany();

        // Then
        assertThat(result).isEqualTo(company);
    }

    @Test
    void shouldReturnStatus_whenGetStatusCalled() {
        // Given
        String status = "New";
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", status);

        // When
        String result = lead.getStatus();

        // Then
        assertThat(result).isEqualTo(status);
    }

    @Test
    void shouldReturnFormattedString_whenToStringCalled() {
        // Given
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Lead lead = new Lead(id, "test@example.com", "+1234567890", "Test Company", "New");

        // When
        String result = lead.toString();

        // Then
        assertThat(result).isEqualTo("Lead{id=" + id + ", email='test@example.com', phone='+1234567890', company='Test Company', status='New'}");
    }

    @Test
    void shouldGenerateRandomId_whenNoIdProvided() {
        // Given
        Lead lead = new Lead("email", "phone", "company", "status");

        // When
        UUID result = lead.getId();

        // Then
        assertThat(result).isNotNull();
    }
}
