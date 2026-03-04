package ru.mentee.power.crm.entity;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealEntityRepository extends JpaRepository<DealEntity, UUID> {

  List<DealEntity> findByStatus(String status);
}
