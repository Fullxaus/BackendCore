package ru.mentee.power.crm.spring.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.entity.LeadEntity;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.spring.dto.UpdateLeadRequest;
import ru.mentee.power.crm.spring.exception.DuplicateEmailException;
import ru.mentee.power.crm.spring.exception.EntityNotFoundException;
import ru.mentee.power.crm.spring.mapper.LeadMapper;

/**
 * Сервис для работы с LeadEntity через JPA репозиторий. Использует новые методы репозитория:
 * derived methods, JPQL, пагинацию и bulk операции.
 */
@Service
public class LeadEntityService {

  private final LeadRepository repository;
  private final LeadMapper leadMapper;

  public LeadEntityService(LeadRepository repository, LeadMapper leadMapper) {
    this.repository = repository;
    this.leadMapper = leadMapper;
  }

  // ========== Simple CRUD helpers for REST layer ==========

  public List<LeadEntity> findAll() {
    return repository.findAll();
  }

  public Optional<LeadEntity> findById(UUID id) {
    return repository.findById(id);
  }

  /** Возвращает лида по ID или выбрасывает EntityNotFoundException. */
  public LeadEntity getLeadById(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Lead", id.toString()));
  }

  /** Создаёт лида с проверкой уникальности email. При дубликате — 409 Conflict. */
  public LeadEntity createLead(LeadEntity entity) {
    if (repository.existsByEmail(entity.getEmail())) {
      throw new DuplicateEmailException(entity.getEmail());
    }
    return repository.save(entity);
  }

  /** Обновляет лида по ID. При отсутствии — EntityNotFoundException (404). */
  public LeadEntity updateLead(UUID id, UpdateLeadRequest request) {
    LeadEntity lead =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Lead", id.toString()));
    leadMapper.updateEntity(request, lead);
    return repository.save(lead);
  }

  /** Удаляет лида по ID. При отсутствии — EntityNotFoundException (404). */
  public void deleteLead(UUID id) {
    if (!repository.existsById(id)) {
      throw new EntityNotFoundException("Lead", id.toString());
    }
    repository.deleteById(id);
  }

  public LeadEntity save(LeadEntity entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  // ========== Simple CRUD helpers for REST layer ==========

  public List<LeadEntity> findAll() {
    return repository.findAll();
  }

  public Optional<LeadEntity> findById(UUID id) {
    return repository.findById(id);
  }

  public LeadEntity save(LeadEntity entity) {
    return repository.save(entity);
  }

  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  // ========== Derived Methods ==========

  /** Поиск лида по email (derived method). */
  public Optional<LeadEntity> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  /** Поиск лидов по статусу. */
  public List<LeadEntity> findByStatus(String status) {
    return repository.findByStatus(status);
  }

  /** Поиск лидов по компании. */
  public List<LeadEntity> findByCompany(String company) {
    return repository.findByCompany(company);
  }

  /** Подсчёт лидов по статусу. */
  public long countByStatus(String status) {
    return repository.countByStatus(status);
  }

  /** Проверка существования по email. */
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  /** Поиск лидов по части email (LIKE запрос). */
  public List<LeadEntity> findByEmailContaining(String emailPart) {
    return repository.findByEmailContaining(emailPart);
  }

  /** Поиск по статусу И компании. */
  public List<LeadEntity> findByStatusAndCompany(String status, String company) {
    return repository.findByStatusAndCompany(status, company);
  }

  /** Поиск с сортировкой по дате создания (от новых к старым). */
  public List<LeadEntity> findByStatusOrderByCreatedAtDesc(String status) {
    return repository.findByStatusOrderByCreatedAtDesc(status);
  }

  // ========== JPQL Queries ==========

  /** Поиск лидов по списку статусов (JPQL). */
  public List<LeadEntity> findByStatuses(String... statuses) {
    return repository.findByStatusIn(List.of(statuses));
  }

  /** Поиск лидов созданных после определённой даты. */
  public List<LeadEntity> findCreatedAfter(Instant date) {
    return repository.findCreatedAfter(date);
  }

  /** Поиск лидов с фильтрацией и сортировкой (JPQL). */
  public List<LeadEntity> findByCompanyOrderedByDate(String company) {
    return repository.findByCompanyOrderedByDate(company);
  }

  // ========== Pagination Methods ==========

  /** Получить первую страницу лидов с сортировкой. */
  public Page<LeadEntity> getFirstPage(int pageSize) {
    PageRequest pageRequest =
        PageRequest.of(
            0, // первая страница (нумерация с 0)
            pageSize,
            Sort.by("createdAt").descending());
    return repository.findAll(pageRequest);
  }

  /** Поиск по статусу с пагинацией. */
  public Page<LeadEntity> findByStatus(String status, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return repository.findByStatus(status, pageable);
  }

  /** Поиск по компании с пагинацией. */
  public Page<LeadEntity> searchByCompany(String company, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return repository.findByCompany(company, pageable);
  }

  /** Поиск по списку статусов с пагинацией. */
  public Page<LeadEntity> findByStatusesPaged(List<String> statuses, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return repository.findByStatusInPaged(statuses, pageable);
  }

  // ========== Bulk Operations ==========

  /**
   * Массовое обновление статуса (используется @Modifying метод). ВАЖНО: @Transactional обязательна
   * для @Modifying!
   */
  @Transactional
  public int convertNewToContacted() {
    int updated = repository.updateStatusBulk("NEW", "CONTACTED");
    // Логируем для observability
    System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
    return updated;
  }

  /** Массовое обновление статуса (общий метод). */
  @Transactional
  public int updateStatusBulk(String oldStatus, String newStatus) {
    int updated = repository.updateStatusBulk(oldStatus, newStatus);
    System.out.printf("Updated %d leads from %s to %s%n", updated, oldStatus, newStatus);
    return updated;
  }

  /** Массовое удаление по статусу. */
  @Transactional
  public int archiveOldLeads(String status) {
    int deleted = repository.deleteByStatusBulk(status);
    System.out.printf("Archived (deleted) %d leads with status %s%n", deleted, status);
    return deleted;
  }
}
