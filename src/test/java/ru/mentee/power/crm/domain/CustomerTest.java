package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

public class CustomerTest {

    @Test
    void shouldReuseContact_whenCreatingCustomer() {
        // Создать Contact и два разных Address (один для contact, один для billing)
        Address contactAddress = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("example@example.com", "+1234567890", contactAddress);
        Address billingAddress = new Address("New York", "456 Broadway", "10001");

        // Создать Customer с contact и billingAddress
        Customer customer = new Customer(UUID.randomUUID(), contact, billingAddress, "GOLD");

        // Проверить что customer.contact().address() != customer.billingAddress()
        assertThat(customer.contact().address()).isNotEqualTo(customer.billingAddress());
    }

    @Test
    void shouldDemonstrateContactReuse_acrossLeadAndCustomer() {
        // Создать одинаковый Contact (email, phone, address)
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("example@example.com", "+1234567890", address);

        // Использовать в Lead и Customer
        Lead lead = new Lead(UUID.randomUUID(), contact, "Example Company", "NEW");
        Customer customer = new Customer(UUID.randomUUID(), contact, new Address("New York", "456 Broadway", "10001"), "GOLD");

        // Продемонстрировать что Contact переиспользуется без дублирования кода
        assertThat(lead.contact()).isEqualTo(contact);
        assertThat(customer.contact()).isEqualTo(contact);
        assertThat(lead.contact().address()).isEqualTo(address);
        assertThat(customer.contact().address()).isEqualTo(address);
    }
}
