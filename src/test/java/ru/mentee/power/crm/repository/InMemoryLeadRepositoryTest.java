package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class InMemoryLeadRepositoryTest {

    @Test
    void shouldAddLead_whenSaveCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Contact contact = new Contact("email", "phone", new Address("city", "street", "zip"));
        Lead lead = new Lead(UUID.randomUUID(), contact, "company", "NEW");

        // When
        repository.save(lead);

        // Then
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void shouldFindById_whenFindByIdCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Contact contact = new Contact("email", "phone", new Address("city", "street", "zip"));
        Lead lead = new Lead(UUID.randomUUID(), contact, "company", "QUALIFIED");
        repository.save(lead);

        // When
        Lead result = repository.findById(lead.id());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(lead.id());
    }

    @Test
    void shouldNotFindById_whenFindByIdCalledWithNonExistingId() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();

        // When
        Lead result = repository.findById(UUID.randomUUID());

        // Then
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnDefensiveCopy_whenFindAllCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Contact contact = new Contact("email", "phone", new Address("city", "street", "zip"));
        Lead lead = new Lead(UUID.randomUUID(), contact, "company", "QUALIFIED");
        repository.save(lead);

        // When
        List<Lead> leads = repository.findAll();
        leads.clear();

        // Then
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void shouldFindByEmail_whenFindByEmailCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Contact contact = new Contact("email", "phone", new Address("city", "street", "zip"));
        Lead lead = new Lead(UUID.randomUUID(), contact, "company", "QUALIFIED");
        repository.save(lead);

        // When
        Optional<Lead> result = repository.findByEmail("email");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(lead.id());
    }

    @Test
    void shouldDeleteLead_whenDeleteCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Contact contact = new Contact("email", "phone", new Address("city", "street", "zip"));
        Lead lead = new Lead(UUID.randomUUID(), contact, "company", "CONVERTED");
        repository.save(lead);

        // When
        repository.delete(lead.id());

        // Then
        assertThat(repository.findAll()).hasSize(0);
    }
}
