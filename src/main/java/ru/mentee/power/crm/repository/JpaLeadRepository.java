package ru.mentee.power.crm.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.entity.LeadEntity;
import ru.mentee.power.crm.model.Lead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("dev")
public class JpaLeadRepository implements LeadDomainRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaLeadRepository.class);
    private final LeadRepository jpaRepository;

    public JpaLeadRepository(LeadRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Lead lead) {
        LeadEntity e = toEntity(lead);
        jpaRepository.save(e);
    }

    @Override
    public Lead findById(UUID id) {
        log.debug("Finding lead by ID: {}", id);
        try {
            return jpaRepository.findById(id)
                    .map(entity -> {
                        log.debug("Found LeadEntity: id={}, email={}, company={}", 
                                entity.getId(), entity.getEmail(), entity.getCompany());
                        return toModel(entity);
                    })
                    .orElse(null);
        } catch (Exception e) {
            log.error("Error finding lead by ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Lead> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Lead> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::toModel);
    }

    private LeadEntity toEntity(Lead lead) {
        LeadEntity e = new LeadEntity();
        e.setId(lead.id());
        e.setEmail(lead.contact().email());
        e.setPhone(lead.contact().phone());
        e.setCompany(lead.company());
        e.setStatus(lead.status());
        if (lead.contact().address() != null) {
            e.setCity(lead.contact().address().city());
            e.setStreet(lead.contact().address().street());
            e.setZip(lead.contact().address().zip());
        }
        return e;
    }

    private Lead toModel(LeadEntity e) {
        try {
            if (e == null) {
                log.error("LeadEntity is null");
                throw new IllegalArgumentException("LeadEntity cannot be null");
            }
            
            if (e.getId() == null) {
                log.error("LeadEntity ID is null");
                throw new IllegalArgumentException("LeadEntity ID cannot be null");
            }
            
            log.debug("Converting LeadEntity to Lead: id={}, email={}, phone={}, company={}, status={}", 
                    e.getId(), e.getEmail(), e.getPhone(), e.getCompany(), e.getStatus());
            
            // Безопасное создание Address с проверкой на null и пустые строки
            String city = (e.getCity() != null && !e.getCity().isEmpty()) ? e.getCity() : "-";
            String street = (e.getStreet() != null && !e.getStreet().isEmpty()) ? e.getStreet() : "-";
            String zip = (e.getZip() != null && !e.getZip().isEmpty()) ? e.getZip() : "-";
            
            log.debug("Creating Address: city={}, street={}, zip={}", city, street, zip);
            Address address;
            try {
                address = new Address(city, street, zip);
            } catch (IllegalArgumentException ex) {
                log.error("Failed to create Address: city={}, street={}, zip={}, error={}", 
                        city, street, zip, ex.getMessage());
                throw ex;
            }
            
            // Безопасное создание Contact с проверкой на null
            String email = (e.getEmail() != null && !e.getEmail().isEmpty()) ? e.getEmail() : "unknown@example.com";
            String phone = (e.getPhone() != null && !e.getPhone().isEmpty()) ? e.getPhone() : "-";
            
            log.debug("Creating Contact: email={}, phone={}", email, phone);
            Contact contact;
            try {
                contact = new Contact(email, phone, address);
            } catch (IllegalArgumentException ex) {
                log.error("Failed to create Contact: email={}, phone={}, error={}", 
                        email, phone, ex.getMessage());
                throw ex;
            }
            
            // Безопасное создание Lead
            String company = (e.getCompany() != null && !e.getCompany().isEmpty()) ? e.getCompany() : "Unknown";
            String status = (e.getStatus() != null && !e.getStatus().isEmpty()) ? e.getStatus() : "NEW";
            
            log.debug("Creating Lead: id={}, company={}, status={}", e.getId(), company, status);
            Lead lead;
            try {
                lead = new Lead(e.getId(), contact, company, status);
            } catch (IllegalArgumentException ex) {
                log.error("Failed to create Lead: id={}, company={}, status={}, error={}", 
                        e.getId(), company, status, ex.getMessage());
                throw ex;
            }
            
            log.debug("Successfully converted LeadEntity to Lead");
            return lead;
        } catch (IllegalArgumentException ex) {
            log.error("IllegalArgumentException while converting LeadEntity to Lead: {}", ex.getMessage(), ex);
            log.error("LeadEntity details: id={}, email={}, phone={}, company={}, status={}, city={}, street={}, zip={}", 
                    e != null ? e.getId() : "null", 
                    e != null ? e.getEmail() : "null", 
                    e != null ? e.getPhone() : "null", 
                    e != null ? e.getCompany() : "null", 
                    e != null ? e.getStatus() : "null", 
                    e != null ? e.getCity() : "null", 
                    e != null ? e.getStreet() : "null", 
                    e != null ? e.getZip() : "null");
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while converting LeadEntity to Lead: {}", ex.getMessage(), ex);
            log.error("LeadEntity details: id={}, email={}, phone={}, company={}, status={}, city={}, street={}, zip={}", 
                    e != null ? e.getId() : "null", 
                    e != null ? e.getEmail() : "null", 
                    e != null ? e.getPhone() : "null", 
                    e != null ? e.getCompany() : "null", 
                    e != null ? e.getStatus() : "null", 
                    e != null ? e.getCity() : "null", 
                    e != null ? e.getStreet() : "null", 
                    e != null ? e.getZip() : "null");
            throw ex;
        }
    }
}
