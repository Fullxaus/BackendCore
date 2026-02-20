package ru.mentee.power.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mentee.power.crm.entity.LeadEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository для работы с LeadEntity.
 * Расширен derived methods, JPQL запросами, пагинацией и bulk операциями.
 */
public interface LeadRepository extends JpaRepository<LeadEntity, UUID> {

    // ========== Derived Methods ==========

    /**
     * Поиск лида по email (точное совпадение).
     * SQL: SELECT * FROM leads WHERE email = ?
     */
    Optional<LeadEntity> findByEmail(String email);

    /**
     * Поиск лидов по статусу.
     * SQL: SELECT * FROM leads WHERE status = ?
     */
    List<LeadEntity> findByStatus(String status);

    /**
     * Поиск лидов по компании.
     * SQL: SELECT * FROM leads WHERE company = ?
     */
    List<LeadEntity> findByCompany(String company);

    /**
     * Подсчёт лидов по статусу.
     * SQL: SELECT COUNT(*) FROM leads WHERE status = ?
     */
    long countByStatus(String status);

    /**
     * Проверка существования по email.
     * SQL: SELECT EXISTS(SELECT 1 FROM leads WHERE email = ?)
     */
    boolean existsByEmail(String email);

    /**
     * Поиск лидов по части email (LIKE запрос).
     * SQL: SELECT * FROM leads WHERE email LIKE '%emailPart%'
     */
    List<LeadEntity> findByEmailContaining(String emailPart);

    /**
     * Поиск по статусу И компании.
     * SQL: SELECT * FROM leads WHERE status = ? AND company = ?
     */
    List<LeadEntity> findByStatusAndCompany(String status, String company);

    /**
     * Поиск с сортировкой по дате создания (от новых к старым).
     * SQL: SELECT * FROM leads WHERE status = ? ORDER BY created_at DESC
     */
    List<LeadEntity> findByStatusOrderByCreatedAtDesc(String status);

    // ========== Native Query ==========

    /**
     * Поиск по email через native SQL запрос.
     */
    @Query(value = "SELECT * FROM leads WHERE email = ?1", nativeQuery = true)
    Optional<LeadEntity> findByEmailNative(String email);

    // ========== JPQL Queries ==========

    /**
     * Поиск лидов по списку статусов (JPQL).
     * JPQL: SELECT l FROM LeadEntity l WHERE l.status IN :statuses
     * SQL: SELECT * FROM leads WHERE status IN (?, ?, ...)
     */
    @Query("SELECT l FROM LeadEntity l WHERE l.status IN :statuses")
    List<LeadEntity> findByStatusIn(@Param("statuses") List<String> statuses);

    /**
     * Поиск лидов созданных после определённой даты.
     * JPQL: SELECT l FROM LeadEntity l WHERE l.createdAt > :date
     */
    @Query("SELECT l FROM LeadEntity l WHERE l.createdAt > :date")
    List<LeadEntity> findCreatedAfter(@Param("date") Instant date);

    /**
     * Поиск лидов с фильтрацией и сортировкой (JPQL).
     */
    @Query("SELECT l FROM LeadEntity l WHERE l.company = :company ORDER BY l.createdAt DESC")
    List<LeadEntity> findByCompanyOrderedByDate(@Param("company") String company);

    // ========== Pagination Methods ==========

    /**
     * Поиск всех лидов с пагинацией (переопределяем из JpaRepository).
     * Клиент: PageRequest.of(0, 20) — первая страница, 20 элементов
     */
    @Override
    Page<LeadEntity> findAll(Pageable pageable);

    /**
     * Поиск по статусу с пагинацией (derived method).
     */
    Page<LeadEntity> findByStatus(String status, Pageable pageable);

    /**
     * Поиск по компании с пагинацией.
     */
    Page<LeadEntity> findByCompany(String company, Pageable pageable);

    /**
     * JPQL запрос с пагинацией.
     */
    @Query("SELECT l FROM LeadEntity l WHERE l.status IN :statuses")
    Page<LeadEntity> findByStatusInPaged(@Param("statuses") List<String> statuses, Pageable pageable);

    // ========== Bulk Operations ==========

    /**
     * Массовое обновление статуса лидов.
     * ВАЖНО: требует @Transactional на уровне Service!
     *
     * @return количество обновлённых строк
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE LeadEntity l SET l.status = :newStatus WHERE l.status = :oldStatus")
    int updateStatusBulk(
            @Param("oldStatus") String oldStatus,
            @Param("newStatus") String newStatus
    );

    /**
     * Массовое удаление по статусу.
     */
    @Modifying
    @Query("DELETE FROM LeadEntity l WHERE l.status = :status")
    int deleteByStatusBulk(@Param("status") String status);
}
