package ru.mentee.power.crm.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mentee.power.crm.entity.DealEntity;

import java.util.Optional;
import java.util.UUID;

public interface DealJpaRepository extends JpaRepository<DealEntity, UUID> {

    @EntityGraph(attributePaths = {"dealProducts", "dealProducts.product"})
    @Query("SELECT d FROM DealEntity d WHERE d.id = :id")
    Optional<DealEntity> findDealWithProducts(@Param("id") UUID id);
}
