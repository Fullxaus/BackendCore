package ru.mentee.power.crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadDomainRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@Transactional
public class LeadService {

    private final LeadDomainRepository repository;
    private final DealRepository dealRepository;
    private final LeadProcessor leadProcessor;

    /** Для standalone (Main) и тестов без Spring-контекста. */
    public LeadService(LeadDomainRepository repository, DealRepository dealRepository) {
        this.repository = repository;
        this.dealRepository = dealRepository;
        this.leadProcessor = null;
    }

    /** Конструктор для Spring: явно выбран для инъекции при наличии LeadProcessor. */
    @Autowired
    public LeadService(LeadDomainRepository repository, DealRepository dealRepository, LeadProcessor leadProcessor) {
        this.repository = repository;
        this.dealRepository = dealRepository;
        this.leadProcessor = leadProcessor;
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

    /**
     * Атомарная конверсия лида в сделку: создаёт Deal и обновляет Lead.status на CONVERTED.
     * При любой ошибке (например, amount = null) транзакция откатывается.
     */
    @Transactional
    public Deal convertLeadToDeal(UUID leadId, BigDecimal amount) {
        Lead lead = repository.findById(leadId);
        if (lead == null) {
            throw new IllegalArgumentException("Lead not found: " + leadId);
        }
        // Сначала обновляем статус лида
        Lead updatedLead = new Lead(lead.id(), lead.contact(), lead.company(), LeadStatus.CONVERTED.name());
        repository.save(updatedLead);
        // Затем создаём сделку (при ошибке здесь откатятся оба изменения)
        Deal deal = new Deal(leadId, amount);
        dealRepository.save(deal);
        return deal;
    }

    /**
     * Обрабатывает список лидов через LeadProcessor — каждый в отдельной транзакции (REQUIRES_NEW).
     */
    public void processLeads(List<UUID> ids) {
        if (leadProcessor != null) {
            for (UUID id : ids) {
                leadProcessor.processSingleLead(id);
            }
        } else {
            processLeadsSelfInvocation(ids);
        }
    }

    /**
     * Демонстрация self-invocation: вызов this.processSingleLead не идёт через proxy,
     * поэтому @Transactional(REQUIRES_NEW) не срабатывает — все операции в одной транзакции.
     */
    public void processLeadsSelfInvocation(List<UUID> ids) {
        for (UUID id : ids) {
            processSingleLead(id);
        }
    }

    /**
     * Метод с readOnly = true: это подсказка БД, а не запрет на запись — save() выполнится.
     */
    @Transactional(readOnly = true)
    public Lead findByIdReadOnly(UUID id) {
        Lead lead = repository.findById(id);
        if (lead != null) {
            // readOnly — hint, не security: запись технически возможна (зависит от драйвера/БД)
            repository.save(lead);
        }
        return lead;
    }

    /**
     * Обрабатывает один лид. При вызове через this (self-invocation) REQUIRES_NEW не работает.
     */
    @Transactional(propagation = REQUIRES_NEW)
    public void processSingleLead(UUID id) {
        Lead lead = repository.findById(id);
        if (lead != null) {
            Lead updated = new Lead(lead.id(), lead.contact(), lead.company(), LeadStatus.PROCESSED.name());
            repository.save(updated);
        }
    }
}

