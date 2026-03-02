package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.entity.LeadEntity;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LeadRepositoryTest {

    @Autowired
    private LeadRepository repository;

    @Test
    void shouldFindByEmailIgnoreCase_whenExists() {
        // Given — в БД Lead с email "Test@Example.com"
        LeadEntity lead = new LeadEntity();
        lead.setEmail("Test@Example.com");
        lead.setPhone("+79991234567");
        lead.setCompanyName("Acme Corp");
        lead.setStatus("NEW");
        lead.setCreatedAt(Instant.now());
        repository.save(lead);

        // When — поиск "test@example.com"
        Optional<LeadEntity> found = repository.findByEmailIgnoreCase("test@example.com");

        // Then — Lead найден, email сохранён как в БД
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("Test@Example.com");
        assertThat(found.get().getCompanyName()).isEqualTo("Acme Corp");
    }

    @Test
    void shouldReturnEmpty_whenEmailNotFound() {
        // When — email не существует
        Optional<LeadEntity> found = repository.findByEmailIgnoreCase("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnOneLead_whenSearchingUniqueEmail() {
        // Given — несколько Lead с разными email
        LeadEntity lead1 = new LeadEntity();
        lead1.setEmail("first@example.com");
        lead1.setPhone("+79991111111");
        lead1.setCompanyName("Company 1");
        lead1.setStatus("NEW");
        lead1.setCreatedAt(Instant.now());
        repository.save(lead1);

        LeadEntity lead2 = new LeadEntity();
        lead2.setEmail("Second@Example.com");
        lead2.setPhone("+79992222222");
        lead2.setCompanyName("Company 2");
        lead2.setStatus("NEW");
        lead2.setCreatedAt(Instant.now());
        repository.save(lead2);

        // When — поиск уникального (case-insensitive)
        Optional<LeadEntity> found = repository.findByEmailIgnoreCase("second@example.com");

        // Then — возвращается один Lead
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("Second@Example.com");
    }
}
