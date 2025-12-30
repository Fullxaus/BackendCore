package ru.mentee.power.crm.model;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class LeadTest {

    @Test
    void shouldReturnId_whenGetIdCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("test@example.com", "+1234567890", new Address("New York", "456 Broadway", "10001"));
        Lead lead = new Lead(id, contact, "TechCorp", "NEW");

        // When
        UUID result = lead.id();

        // Then
        assertThat(result).isEqualTo(id);
    }

    @Test
    void shouldReturnContact_whenGetContactCalled() {
        // Given
        Contact contact = new Contact("test@example.com", "+1234567890", new Address("New York", "456 Broadway", "10001"));
        Lead lead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");

        // When
        Contact result = lead.contact();

        // Then
        assertThat(result).isEqualTo(contact);
    }

    @Test
    void shouldReturnCompany_whenGetCompanyCalled() {
        // Given
        String company = "Test Company";
        Lead lead = new Lead(UUID.randomUUID(), new Contact("test@example.com", "+1234567890", new Address("New York", "456 Broadway", "10001")), company, "NEW");

        // When
        String result = lead.company();

        // Then
        assertThat(result).isEqualTo(company);
    }

    @Test
    void shouldReturnStatus_whenGetStatusCalled() {
        // Given
        String status = "NEW";
        Lead lead = new Lead(UUID.randomUUID(), new Contact("test@example.com", "+1234567890", new Address("New York", "456 Broadway", "10001")), "TechCorp", status);

        // When
        String result = lead.status();

        // Then
        assertThat(result).isEqualTo(status);
    }

    @Test
    void shouldReturnFormattedString_whenToStringCalled() {
        // Given
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("test@example.com", "+1234567890", new Address("New York", "456 Broadway", "10001"));
        Lead lead = new Lead(id, contact, "Test Company", "NEW");

        // When
        String result = lead.toString();

        // Then
        assertThat(result).contains(String.valueOf(id));
        assertThat(result).contains("test@example.com");
        assertThat(result).contains("+1234567890");
        assertThat(result).contains("Test Company");
        assertThat(result).contains("NEW");
    }

}
