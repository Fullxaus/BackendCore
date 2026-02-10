package ru.mentee.power.crm.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LeadEntityRepository extends JpaRepository<LeadEntity, UUID> {

    Optional<LeadEntity> findByEmail(String email);
}
