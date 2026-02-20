package ru.mentee.power.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadDomainRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class LeadServiceTest {

    private LeadService service;
    private LeadDomainRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLeadRepository();
        service = new LeadService(repository);
    }

    @Test
    void shouldCreateLead_whenEmailIsUnique() {
        // Given
        String email = "test@example.com";
        String company = "Test Company";
        LeadStatus status = LeadStatus.NEW;
        Address address = new Address("Test City", "Test Street", "12345");
        String phone = "+1234567890";

        // When
        Lead result = service.addLead(email, company, status, address, phone);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.contact().email()).isEqualTo(email);
        assertThat(result.contact().phone()).isEqualTo(phone);
        assertThat(result.company()).isEqualTo(company);
        assertThat(result.status()).isEqualTo(status.name());
        assertThat(result.id()).isNotNull();
        assertThat(result.contact().address().city()).isEqualTo("Test City");
        assertThat(result.contact().address().zip()).isEqualTo("12345");
    }


    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        // Given
        String email = "duplicate@example.com";
        Address address = new Address("Test City", "Test Street", "12345");
        String phone = "+1234567890";
        service.addLead(email, "First Company", LeadStatus.NEW, address, phone);

        // When/Then
        Address anotherAddress = new Address("Another City", "Another Street", "67890");
        assertThatThrownBy(() ->
                service.addLead(email, "Second Company", LeadStatus.NEW, anotherAddress, phone)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Lead with email already exists");
    }

    @Test
    void shouldFindAllLeads() {
        // Given
        Address address1 = new Address("Test City1", "Test Street1", "12345");
        String phone1 = "+1234567890";
        service.addLead("one@example.com", "Company 1", LeadStatus.NEW, address1, phone1);
        Address address2 = new Address("Test City2", "Test Street2", "67890");
        String phone2 = "+9876543210";
        service.addLead("two@example.com", "Company 2", LeadStatus.NEW, address2, phone2);

        // When
        List<Lead> result = service.findAll();

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldFindLeadById() {
        // Given
        Address address = new Address("Test City", "Test Street", "12345");
        String phone = "+1234567890";
        Lead created = service.addLead("find@example.com", "Company", LeadStatus.NEW, address, phone);

        // When
        Optional<Lead> result = service.findById(created.id());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().contact().email()).isEqualTo("find@example.com");
    }

    @Test
    void shouldFindLeadByEmail() {
        // Given
        Address address = new Address("Test City", "Test Street", "12345");
        String phone = "+1234567890";
        service.addLead("search@example.com", "Company", LeadStatus.NEW, address, phone);

        // When
        Optional<Lead> result = service.findByEmail("search@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().company()).isEqualTo("Company");
    }

    @Test
    void shouldReturnEmpty_whenLeadNotFound() {
        // Given/When
        Optional<Lead> result = service.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOnlyNewLeads_whenFindByStatusNew() {
        // Given
        LeadDomainRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);

        leadService.addLead("test1@example.com", "Company1", LeadStatus.NEW, new Address("Moscow", "Suvorova", "123456"), "1234567890");
        leadService.addLead("test2@example.com", "Company2", LeadStatus.NEW, new Address("St.Petersburg", "Pushkinskaya", "987654"), "9876543210");
        leadService.addLead("test3@example.com", "Company3", LeadStatus.NEW, new Address("Kazan", "Kazanskaya", "111111"), "1111111111");

        // When
        List<Lead> result = leadService.findByStatus(LeadStatus.NEW);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(lead -> lead.status().equals(LeadStatus.NEW.name()));
    }

    @Test
    void shouldReturnEmptyList_whenNoLeadsWithStatus() {
        // Given
        LeadDomainRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);

        // When
        List<Lead> result = leadService.findByStatus(LeadStatus.NEW);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateLead_inRepository() {
        // Given: existing lead
        Address address = new Address("City", "Street", "12345");
        Lead created = service.addLead("old@example.com", "OldCo", LeadStatus.NEW, address, "+7999");
        UUID id = created.id();

        // When: update with new data
        Lead updated = service.update(id, "new@example.com", "+7111", "NewCo", LeadStatus.CONTACTED);

        // Then: repository contains updated data
        assertThat(updated.contact().email()).isEqualTo("new@example.com");
        assertThat(updated.contact().phone()).isEqualTo("+7111");
        assertThat(updated.company()).isEqualTo("NewCo");
        assertThat(updated.status()).isEqualTo(LeadStatus.CONTACTED.name());
        assertThat(updated.id()).isEqualTo(id);

        Optional<Lead> fromRepo = service.findById(id);
        assertThat(fromRepo).isPresent();
        assertThat(fromRepo.get().company()).isEqualTo("NewCo");
    }

    @Test
    void shouldThrow_whenUpdatingNonexistentLead() {
        UUID nonexistentId = UUID.randomUUID();

        assertThatThrownBy(() ->
                service.update(nonexistentId, "x@x.com", "+7", "Co", LeadStatus.NEW))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Lead not found");
    }

    @Test
    void shouldDeleteLead_whenExists() {
        Address address = new Address("City", "Street", "12345");
        Lead created = service.addLead("delete@example.com", "ToDeleteCo", LeadStatus.NEW, address, "+7999");
        UUID id = created.id();
        assertThat(service.findById(id)).isPresent();

        service.delete(id);

        assertThat(service.findById(id)).isEmpty();
    }

    @Test
    void shouldThrow404_whenDeletingNonexistentLead() {
        UUID nonexistentId = UUID.randomUUID();

        assertThatThrownBy(() -> service.delete(nonexistentId))
                .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                .satisfies(ex -> {
                    org.springframework.web.server.ResponseStatusException rse =
                            (org.springframework.web.server.ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
                });
    }
}