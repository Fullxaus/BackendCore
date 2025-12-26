package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class LeadRepositoryTest {
    private LeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LeadRepository();
    }

    @Test
    void shouldSaveAndFindLeadById_whenLeadSaved() {
        // Given
        UUID id = UUID.randomUUID();
        Lead lead = new Lead(id, "email", "phone", "company", "status");

        // When
        repository.save(lead);

        // Then
        assertThat(repository.findById(id)).isNotNull();
    }

    @Test
    void shouldReturnNull_whenLeadNotFound() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        Lead lead = repository.findById(id);

        // Then
        assertThat(lead).isNull();
    }

    @Test
    void shouldReturnAllLeads_whenMultipleLeadsSaved() {
        // Given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        repository.save(new Lead(id1, "email1", "phone1", "company1", "status1"));
        repository.save(new Lead(id2, "email2", "phone2", "company2", "status2"));

        // When
        List<Lead> leads = repository.findAll();

        // Then
        assertThat(leads).hasSize(2);
    }

    @Test
    void shouldDeleteLead_whenLeadExists() {
        // Given
        UUID id = UUID.randomUUID();
        Lead lead = new Lead(id, "email", "phone", "company", "status");
        repository.save(lead);

        // When
        repository.delete(id);

        // Then
        assertThat(repository.findById(id)).isNull();
        assertThat(repository.size()).isEqualTo(0);
    }

    @Test
    void shouldOverwriteLead_whenSaveWithSameId() {
        // Given
        UUID id = UUID.randomUUID();
        Lead lead1 = new Lead(id, "email1", "phone1", "company1", "status1");
        Lead lead2 = new Lead(id, "email2", "phone2", "company2", "status2");

        // When
        repository.save(lead1);
        repository.save(lead2);

        // Then
        assertThat(repository.findById(id).email()).isEqualTo("email2");
        assertThat(repository.size()).isEqualTo(1);
    }

    @Test
    void shouldFindFasterWithMap_thanWithListFilter() {
        // Given: Создать 1000 лидов
        List<Lead> leadList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            UUID id = UUID.randomUUID();
            String email = "email" + i + "@test.com";
            String phone = "+7" + i;
            String company = "Company" + i;
            String status = "NEW";
            Lead lead = new Lead(id, email, phone, company, status);
            repository.save(lead);
            leadList.add(lead);
        }

        // Id среднего элемента
        UUID targetId = leadList.get(500).id();

        // When: Поиск через Map
        long mapStart = System.nanoTime();
        Lead foundInMap = repository.findById(targetId);
        long mapDuration = System.nanoTime() - mapStart;

        // When: Поиск через List.stream().filter()
        long listStart = System.nanoTime();
        Lead foundInList = leadList.stream()
                .filter(lead -> lead.id().equals(targetId))
                .findFirst()
                .orElse(null);
        long listDuration = System.nanoTime() - listStart;

        // Then: Map должен быть минимум в 10 раз быстрее
        assertThat(foundInMap).isEqualTo(foundInList);
        assertThat(listDuration).isGreaterThan(mapDuration * 10);

        System.out.println("Map поиск: " + mapDuration + " ns");
        System.out.println("List поиск: " + listDuration + " ns");
        System.out.println("Ускорение: " + (listDuration / (double) mapDuration) + "x");
    }

    @Test
    void shouldSaveBothLeads_evenWithSameEmailAndPhone_becauseRepositoryDoesNotCheckBusinessRules() {
        // Given: два лида с разными UUID но одинаковыми контактами
        Contact sharedContact = new Contact("Ivan", "Petrov", "ivan@mail.ru");
        Lead originalLead = new Lead(UUID.randomUUID(), sharedContact.email(), "+79001234567", "Acme Corp", "NEW");
        Lead duplicateLead = new Lead(UUID.randomUUID(), sharedContact.email(), "+79001234567", "TechCorp", "HOT");

        // When: сохраняем оба
        repository.save(originalLead);
        repository.save(duplicateLead);

        // Then: Repository сохранил оба (это технически правильно!)
        assertThat(repository.size()).isEqualTo(2);

        // But: Бизнес недоволен — в CRM два контакта на одного человека
        // Решение: Service Layer в Sprint 5 будет проверять бизнес-правила
        // перед вызовом repository.save()
    }




}
