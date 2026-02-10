package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


public class LeadRepositoryTest {
    private LeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLeadRepository();
    }

    @Test
    void shouldSaveAndFindLeadById_whenLeadSaved() {
        // Дано
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+79001234567", new Address("улица", "дом", "10001"));
        Lead lead = new Lead(id, contact, "Acme Corp", "NEW");

        // Когда
        repository.save(lead);

        // Тогда
        assertThat(repository.findById(id)).isNotNull();
    }


    @Test
    void shouldReturnNull_whenLeadNotFound() {
        // Дано
        UUID id = UUID.randomUUID();

        // Когда
        Lead lead = repository.findById(id);

        // Тогда
        assertThat(lead).isNull();
    }


    @Test
    void shouldReturnAllLeads_whenMultipleLeadsSaved() {
        // Дано
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Contact contact1 = new Contact("ivan@mail.ru", "+79001234567", new Address("Boston", "Smith", "111"));
        Contact contact2 = new Contact("ivan@mail.ru", "+79001234567", new Address("London", "QueenStreet", "001100"));
        repository.save(new Lead(id1, contact1, "Acme Corp", "NEW"));
        repository.save(new Lead(id2, contact2, "TechCorp", "QUALIFIED"));

        // Когда
        List<Lead> leads = repository.findAll();

        // Тогда
        assertThat(leads).hasSize(2);
    }

    @Test
    void shouldDeleteLead_whenLeadExists() {
        // Дано
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+79001234567", new Address("Boston", "Smith", "111"));
        Lead lead = new Lead(id, contact, "Acme Corp", "NEW");
        repository.save(lead);

        // Когда
        repository.delete(id);

        // Тогда
        assertThat(repository.findById(id)).isNull();
        assertThat(repository.size()).isEqualTo(0);
    }


    @Test
    void shouldOverwriteLead_whenSaveWithSameId() {
        // Дано
        UUID id = UUID.randomUUID();
        Contact contact1 = new Contact("ivan@mail.ru", "+79001234567", new Address("Boston", "Smith", "111"));
        Contact contact2 = new Contact("ivan@mail.ru", "+79001234567", new Address("London", "QueenStreet", "001100"));
        Lead lead1 = new Lead(id, contact1, "Acme Corp", "NEW");
        Lead lead2 = new Lead(id, contact2, "TechCorp", "QUALIFIED");

        // Когда
        repository.save(lead1);
        repository.save(lead2);

        // Тогда
        assertThat(repository.findById(id).contact().email()).isEqualTo(contact2.email());
        assertThat(repository.size()).isEqualTo(1);
    }

    @Test
    void shouldFindFasterWithMap_thanWithListFilter() {
        // Given: Создать 1000 лидов
        List<Lead> leadList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            UUID id = UUID.randomUUID();
            Contact contact = new Contact("ivan@mail.ru", "+79001234567", new Address("Boston", "Smith", "111"));
            Lead lead = new Lead(id, contact, "Company" + i, "NEW");
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
        Contact sharedContact = new Contact("ivan@mail.ru", "+79001234567", new Address("Boston", "Smith", "111"));
        Lead originalLead = new Lead(UUID.randomUUID(), sharedContact, "Acme Corp", "NEW");
        Lead duplicateLead = new Lead(UUID.randomUUID(), sharedContact, "TechCorp", "CONVERTED");

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
