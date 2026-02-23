package ru.mentee.power.crm.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mentee.power.crm.entity.Company;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository для Company. findByIdWithLeads решает N+1 через @EntityGraph.
 */
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    /**
     * Загрузка компании с лидами одним запросом (LEFT JOIN), без N+1.
     */
    @EntityGraph(attributePaths = {"leads"})
    @Query("SELECT c FROM Company c WHERE c.id = :id")
    Optional<Company> findByIdWithLeads(@Param("id") UUID id);

    Optional<Company> findByName(String name);
}
