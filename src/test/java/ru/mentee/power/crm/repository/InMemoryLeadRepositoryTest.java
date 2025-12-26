package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Lead;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryLeadRepositoryTest {

    @Test
    void shouldAddLead_whenAddCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", "status");

        // When
        repository.add(lead);

        // Then
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void shouldNotAddDuplicateLead_whenAddCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", "status");
        repository.add(lead);

        // When
        repository.add(lead);

        // Then
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void shouldRemoveLead_whenRemoveCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", "status");
        repository.add(lead);

        // When
        repository.remove(lead.id());

        // Then
        assertThat(repository.findAll()).hasSize(0);
    }

    @Test
    void shouldFindById_whenFindByIdCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", "status");
        repository.add(lead);

        // When
        Optional<Lead> result = repository.findById(lead.id());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(lead.id());
    }

    @Test
    void shouldNotFindById_whenFindByIdCalledWithNonExistingId() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();

        // When
        Optional<Lead> result = repository.findById(UUID.randomUUID());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnDefensiveCopy_whenFindAllCalled() {
        // Given
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", "status");
        repository.add(lead);

        // When
        List<Lead> leads = repository.findAll();
        leads.clear();

        // Then
        assertThat(repository.findAll()).hasSize(1);
    }
}
