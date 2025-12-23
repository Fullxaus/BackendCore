package ru.mentee.power.crm.storage;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Address;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class LeadStorageTest {

    @Test
    void shouldAddLead_whenLeadIsUnique() {
        // Given
        LeadStorage storage = new LeadStorage();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("ivan@mail.ru", "+7123", address);
        Lead uniqueLead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");

        // When
        boolean added = storage.add(uniqueLead);

        // Then
        assertThat(added).isTrue();
        assertThat(storage.size()).isEqualTo(1);
        assertThat(storage.findAll()).containsExactly(uniqueLead);
    }

    @Test
    void shouldRejectDuplicate_whenEmailAlreadyExists() {
        // Given
        LeadStorage storage = new LeadStorage();
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("ivan@mail.ru", "+7123", address);
        Lead existingLead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");
        Address anotherAddress = new Address("New York", "456 Broadway", "10001");
        Contact anotherContact = new Contact("ivan@mail.ru", "+7456", anotherAddress);
        Lead duplicateLead = new Lead(UUID.randomUUID(), anotherContact, "Other", "NEW");
        storage.add(existingLead);

        // When
        boolean added = storage.add(duplicateLead);

        // Then
        assertThat(added).isFalse();
        assertThat(storage.size()).isEqualTo(1);
        assertThat(storage.findAll()).containsExactly(existingLead);
    }

    @Test
    void shouldThrowException_whenStorageIsFull() {
        // Given: Заполни хранилище 100 лидами
        LeadStorage storage = new LeadStorage();
        for (int index = 0; index < 100; index++) {
            Address address = new Address("San Francisco", "123 Main St", "94105");
            Contact contact = new Contact("lead" + index + "@mail.ru", "+7000", address);
            storage.add(new Lead(UUID.randomUUID(), contact, "Company", "NEW"));
        }

        // When + Then: 101-й лид должен выбросить исключение
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("lead101@mail.ru", "+7001", address);
        Lead hundredFirstLead = new Lead(UUID.randomUUID(), contact, "Company", "NEW");

        assertThatThrownBy(() -> storage.add(hundredFirstLead))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Storage is full");
    }

    @Test
    void shouldReturnOnlyAddedLeads_whenFindAllCalled() {
        // Given
        LeadStorage storage = new LeadStorage();
        Address address1 = new Address("San Francisco", "123 Main St", "94105");
        Contact contact1 = new Contact("ivan@mail.ru", "+7123", address1);
        Lead firstLead = new Lead(UUID.randomUUID(), contact1, "TechCorp", "NEW");

        Address address2 = new Address("New York", "456 Broadway", "10001");
        Contact contact2 = new Contact("maria@startup.io", "+7456", address2);
        Lead secondLead = new Lead(UUID.randomUUID(), contact2, "StartupLab", "NEW");
        storage.add(firstLead);
        storage.add(secondLead);

        // When
        Lead[] result = storage.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(firstLead, secondLead);
    }
}
