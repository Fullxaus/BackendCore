package ru.mentee.power.crm.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadDomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeadService {

    private final LeadDomainRepository repository;

    // DI через конструктор — не создаём repository внутри!
    public LeadService(LeadDomainRepository repository) {
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

    /**
     * Поиск лидов по тексту (имя/компания или email) и статусу через Stream API.
     */
    public List<Lead> findLeads(String search, String status) {
        return repository.findAll().stream()
                .filter(lead -> search == null || search.isBlank()
                        || lead.company().toLowerCase().contains(search.toLowerCase())
                        || lead.contact().email().toLowerCase().contains(search.toLowerCase()))
                .filter(lead -> status == null || status.isBlank() || lead.status().equals(status))
                .collect(Collectors.toList());
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

    public List<Lead> findByStatus(LeadStatus status) {
        return repository.findAll().stream()
                .filter(lead -> lead.status().equals(status.name())) // Обратите внимание на .name()
                .collect(Collectors.toList());
    }



    /**
     * Обновляет существующего лида по ID.
     *
     * @throws IllegalStateException если лид с указанным ID не найден
     */
    public Lead update(UUID id, String email, String phone, String company, LeadStatus status) {
        Lead existing = repository.findById(id);
        if (existing == null) {
            throw new IllegalStateException("Lead not found: " + id);
        }
        Address address = existing.contact().address();
        Contact contact = new Contact(email, phone, address);
        Lead updated = new Lead(id, contact, company, status.name());
        repository.save(updated);
        return updated;
    }



    /**
     * Удаляет лида по ID. Если лид не найден — выбрасывает ResponseStatusException(404).
     */
    public void delete(UUID id) {
        if (repository.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found");
        }
        repository.delete(id);
    }
}

