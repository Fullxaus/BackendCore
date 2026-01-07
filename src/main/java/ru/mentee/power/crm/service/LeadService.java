package ru.mentee.power.crm.service;

import org.springframework.stereotype.Service;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepository repository;

    // DI через конструктор — не создаём repository внутри!
    public LeadService(LeadRepository repository) {
        this.repository = repository;
    }

    /**
     * Создаёт нового лида с проверкой уникальности email.
     *
     * @throws IllegalStateException если лид с таким email уже существует
     */
    public Lead addLead(String email, String company, LeadStatus status, Address address, String phone) {
        // Бизнес-правило: проверка уникальности email
        Optional<Lead> existing = repository.findByEmail(email);
        if (existing.isPresent()) {
            throw new IllegalStateException("Lead with email already exists: " + email);
        }

        // Создаём нового лида
        Lead lead = new Lead(
                UUID.randomUUID(),
                new Contact(email, phone, address),
                company,
                status.name()
        );

        // Сохраняем через repository
        repository.save(lead);
        return lead;
    }


    public List<Lead> findAll() {
        // Делегирование вызова в repository
        return repository.findAll();
    }

    public Optional<Lead> findById(UUID id) {
        // Делегирование вызова в repository
        return Optional.ofNullable(repository.findById(id));
    }

    public Optional<Lead> findByEmail(String email) {
        // Поиск лида по email
        return repository.findAll().stream()
                .filter(lead -> lead.contact().email().equals(email))
                .findFirst();
    }
}

