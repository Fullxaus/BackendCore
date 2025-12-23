package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

public class LeadTest {

    @Test
    void shouldCreateLead_whenValidData() {
        // Создать Address
        Address address = new Address("San Francisco", "123 Main St", "94105");

        // Создать Contact с Address
        Contact contact = new Contact("example@example.com", "+1234567890", address);

        // Создать Lead с UUID, Contact, company, status
        Lead lead = new Lead(UUID.randomUUID(), contact, "Example Company", "NEW");

        // Проверить что lead.contact() возвращает правильный Contact
        assertThat(lead.contact()).isEqualTo(contact);
    }

    @Test
    void shouldAccessEmailThroughDelegation_whenLeadCreated() {
        // Создать Lead с полной композицией (Lead → Contact → Address)
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("example@example.com", "+1234567890", address);
        Lead lead = new Lead(UUID.randomUUID(), contact, "Example Company", "NEW");

        // Получить email через делегацию: lead.contact().email()
        String email = lead.contact().email();

        // Проверить что email правильный
        assertThat(email).isEqualTo("example@example.com");

        // Получить city через делегацию: lead.contact().address().city()
        String city = lead.contact().address().city();

        // Проверить что city правильный
        assertThat(city).isEqualTo("San Francisco");
    }

    @Test
    void shouldBeEqual_whenSameIdButDifferentContact() {
        // Создать два Lead с одинаковым UUID, но разными Contact
        UUID id = UUID.randomUUID();
        Address address1 = new Address("San Francisco", "123 Main St", "94105");
        Contact contact1 = new Contact("example1@example.com", "+1234567890", address1);
        Lead lead1 = new Lead(id, contact1, "Example Company", "NEW");

        Address address2 = new Address("New York", "456 Broadway", "10001");
        Contact contact2 = new Contact("example2@example.com", "+9876543210", address2);
        Lead lead2 = new Lead(id, contact2, "Another Company", "QUALIFIED");

        // equals по id
        assertThat(lead1).isEqualTo(lead2);
    }

    @Test
    void shouldThrowException_whenContactIsNull() {
        // Проверить что создание Lead с contact=null бросает IllegalArgumentException
        assertThatThrownBy(() -> new Lead(UUID.randomUUID(), null, "Example Company", "NEW"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenInvalidStatus() {
        // Проверить что создание Lead с status="INVALID" бросает IllegalArgumentException
        Contact contact = new Contact("example@example.com", "+1234567890", new Address("San Francisco", "123 Main St", "94105"));
        assertThatThrownBy(() -> new Lead(UUID.randomUUID(), contact, "Example Company", "INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDemonstrateThreeLevelComposition_whenAccessingCity() {
        // Создать полную композицию Lead → Contact → Address
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("example@example.com", "+1234567890", address);
        Lead lead = new Lead(UUID.randomUUID(), contact, "Example Company", "NEW");

        // Продемонстрировать трёхуровневую делегацию
        String city = lead.contact().address().city();

        // Проверить что city правильный
        assertThat(city).isEqualTo("San Francisco");
    }
}
