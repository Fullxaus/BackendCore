package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class ContactTest {

    @Test
    void shouldCreateContact_whenValidData() {
        // Создать Contact с email="john@example.com", phone="1234567890", address=new Address("New York", "456 Broadway", "10001")
        Address address = new Address("New York", "456 Broadway", "10001");
        Contact contact = new Contact("john@example.com", "1234567890", address);

        // Проверить что все компоненты возвращают правильные значения
        assertThat(contact.email()).isEqualTo("john@example.com");
        assertThat(contact.phone()).isEqualTo("1234567890");
        assertThat(contact.address()).isEqualTo(address);
    }

    @Test
    void shouldThrowException_whenEmailIsNullOrEmpty() {
        // Given
        Address address = new Address("New York", "456 Broadway", "10001");

        // When и Then
        assertThatThrownBy(() -> new Contact(null, "1234567890", address))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Contact("", "1234567890", address))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenPhoneIsNullOrEmpty() {
        // Given
        Address address = new Address("New York", "456 Broadway", "10001");

        // When и Then
        assertThatThrownBy(() -> new Contact("john@example.com", null, address))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Contact("john@example.com", "", address))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenAddressIsNull() {
        // Given

        // When и Then
        assertThatThrownBy(() -> new Contact("john@example.com", "1234567890", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
