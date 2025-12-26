package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class ContactTest {

    @Test
    void shouldCreateContact_whenValidData() {
        // Создать Address
        Address address = new Address("San Francisco", "123 Main St", "94105");

        // Создать Contact с email, phone, address
        Contact contact = new Contact("example@example.com", "+1234567890", address);

        // Проверить что contact.address() возвращает правильный Address
        assertThat(contact.address()).isEqualTo(address);

        // Проверить делегацию: contact.address().city() возвращает город
        assertThat(contact.address().city()).isEqualTo("San Francisco");
    }

    @Test
    void shouldDelegateToAddress_whenAccessingCity() {
        // Создать Contact с Address
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("example@example.com", "+1234567890", address);

        // Проверить что contact.address().city() работает корректно
        assertThat(contact.address().city()).isEqualTo("San Francisco");

        // Проверить что contact.address().street() работает корректно
        assertThat(contact.address().street()).isEqualTo("123 Main St");
    }

    @Test
    void shouldThrowException_whenAddressIsNull() {
        // Проверить что создание Contact с address=null бросает IllegalArgumentException
        assertThatThrownBy(() -> new Contact("example@example.com", "+1234567890", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenEmailIsBlank() {
        // Проверить что создание Contact с email="" бросает IllegalArgumentException
        Address address = new Address("San Francisco", "123 Main St", "94105");
        assertThatThrownBy(() -> new Contact("", "+1234567890", address))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenPhoneIsBlank() {
        // Проверить что создание Contact с phone="" бросает IllegalArgumentException
        Address address = new Address("San Francisco", "123 Main St", "94105");
        assertThatThrownBy(() -> new Contact("example@example.com", "", address))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
