package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

public class CustomerTest {

    @Test
    void shouldCreateCustomer() {
        // Given
        Contact contact = new Contact("John", "Doe", "example@example.com");
        Address billingAddress = new Address("New York", "456 Broadway", "10001");

        // When
        Customer customer = new Customer(UUID.randomUUID(), contact, billingAddress, "GOLD");

        // Then
        assertThat(customer.contact()).isEqualTo(contact);
        assertThat(customer.billingAddress()).isEqualTo(billingAddress);
        assertThat(customer.loyaltyTier()).isEqualTo("GOLD");
    }

    @Test
    void shouldNotCreateCustomerWithInvalidLoyaltyTier() {
        // Given
        Contact contact = new Contact("John", "Doe", "example@example.com");
        Address billingAddress = new Address("New York", "456 Broadway", "10001");

        // When и Then
        assertThatThrownBy(() -> new Customer(UUID.randomUUID(), contact, billingAddress, "INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotCreateCustomerWithNullContact() {
        // Given
        Address billingAddress = new Address("New York", "456 Broadway", "10001");

        // When и Then
        assertThatThrownBy(() -> new Customer(UUID.randomUUID(), null, billingAddress, "GOLD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotCreateCustomerWithNullBillingAddress() {
        // Given
        Contact contact = new Contact("John", "Doe", "example@example.com");

        // When и Then
        assertThatThrownBy(() -> new Customer(UUID.randomUUID(), contact, null, "GOLD"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
