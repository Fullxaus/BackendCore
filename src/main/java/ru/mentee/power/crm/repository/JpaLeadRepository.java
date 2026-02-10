package ru.mentee.power.crm.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.entity.LeadEntity;
import ru.mentee.power.crm.entity.LeadEntityRepository;
import ru.mentee.power.crm.model.Lead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("dev")
public class JpaLeadRepository implements LeadRepository {

    private final LeadEntityRepository entityRepository;

    public JpaLeadRepository(LeadEntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Override
    public void save(Lead lead) {
        LeadEntity e = toEntity(lead);
        entityRepository.save(e);
    }

    @Override
    public Lead findById(UUID id) {
        return entityRepository.findById(id)
                .map(this::toModel)
                .orElse(null);
    }

    @Override
    public List<Lead> findAll() {
        return entityRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        entityRepository.deleteById(id);
    }

    @Override
    public Optional<Lead> findByEmail(String email) {
        return entityRepository.findByEmail(email)
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
        Address address = new Address(
                e.getCity() != null ? e.getCity() : "",
                e.getStreet() != null ? e.getStreet() : "",
                e.getZip() != null ? e.getZip() : ""
        );
        Contact contact = new Contact(e.getEmail(), e.getPhone(), address);
        return new Lead(e.getId(), contact, e.getCompany(), e.getStatus());
    }
}
