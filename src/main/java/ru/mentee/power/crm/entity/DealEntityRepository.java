package ru.mentee.power.crm.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DealEntityRepository extends JpaRepository<DealEntity, UUID> {

    List<DealEntity> findByStatus(String status);
}
