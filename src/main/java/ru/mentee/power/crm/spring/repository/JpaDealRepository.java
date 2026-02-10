package ru.mentee.power.crm.spring.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.entity.DealEntity;
import ru.mentee.power.crm.entity.DealEntityRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("dev")
public class JpaDealRepository implements DealRepository {

    private final DealEntityRepository entityRepository;

    public JpaDealRepository(DealEntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Override
    public void save(Deal deal) {
        entityRepository.save(toEntity(deal));
    }

    @Override
    public Optional<Deal> findById(UUID id) {
        return entityRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<Deal> findAll() {
        return entityRepository.findAll().stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public List<Deal> findByStatus(DealStatus status) {
        return entityRepository.findByStatus(status.name()).stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        entityRepository.deleteById(id);
    }

    private DealEntity toEntity(Deal deal) {
        DealEntity e = new DealEntity();
        e.setId(deal.getId());
        e.setLeadId(deal.getLeadId());
        e.setAmount(deal.getAmount());
        e.setStatus(deal.getStatus().name());
        e.setCreatedAt(deal.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
        return e;
    }

    private Deal toModel(DealEntity e) {
        LocalDateTime createdAt = LocalDateTime.ofInstant(
                e.getCreatedAt() != null ? e.getCreatedAt() : Instant.now(),
                ZoneId.systemDefault()
        );
        return new Deal(
                e.getId(),
                e.getLeadId(),
                e.getAmount(),
                DealStatus.valueOf(e.getStatus()),
                createdAt
        );
    }
}
