package ru.mentee.power.crm.storage;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class LeadStorageTest {

    @Test
    void shouldAddLead_whenLeadIsUnique() {
        // Given
        LeadStorage storage = new LeadStorage();
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
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
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead existingLead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");
        Contact anotherContact = new Contact("ivan@mail.ru", "+7456", new Address("Moscow", "789 Street", "20001"));
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
    void shouldReturnOnlyAddedLeads_whenFindAllCalled() {
        // Given
        LeadStorage storage = new LeadStorage();
        Contact contact1 = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead firstLead = new Lead(UUID.randomUUID(), contact1, "TechCorp", "NEW");
        Contact contact2 = new Contact("maria@startup.io", "+7456", new Address("Moscow", "789 Street", "20001"));
        Lead secondLead = new Lead(UUID.randomUUID(), contact2, "StartupLab", "NEW");
        storage.add(firstLead);
        storage.add(secondLead);

        // When
        List<Lead> result = storage.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(firstLead, secondLead);
    }

    @Test
    void shouldNotAddDuplicateLeads() {
        // Given: Заполни хранилище 100 лидами
        LeadStorage storage = new LeadStorage();
        for (int index = 0; index < 100; index++) {
            Contact contact = new Contact("lead" + index + "@mail.ru", "+7000", new Address("New York", "456 Broadway", "10001"));
            Lead lead = new Lead(UUID.randomUUID(), contact, "Company", "NEW");
            storage.add(lead);
        }

        // When + Then: 101-й лид с таким же email не должен добавиться
        Contact contact = new Contact("lead0@mail.ru", "+7001", new Address("New York", "456 Broadway", "10001"));
        Lead hundredFirstLead = new Lead(UUID.randomUUID(), contact, "Company", "NEW");

        assertThat(storage.add(hundredFirstLead)).isFalse();
    }

}
