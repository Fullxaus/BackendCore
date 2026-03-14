package ru.mentee.power.crm.spring.rest.fixed;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.domain.Invitee;
import ru.mentee.power.crm.spring.exception.EmailAlreadyExistsException;
import ru.mentee.power.crm.spring.exception.EntityNotFoundException;
import ru.mentee.power.crm.spring.exception.InvalidStatusException;
import ru.mentee.power.crm.spring.repository.InviteeRepository;
import ru.mentee.power.crm.spring.rest.fixed.dto.CreateInviteeRequest;
import ru.mentee.power.crm.spring.rest.fixed.dto.InviteeResponse;
import ru.mentee.power.crm.spring.rest.fixed.dto.UpdateInviteeStatusRequest;

/**
 * Сервис: вся бизнес-логика вынесена из контроллера.
 *
 * @see ru.mentee.power.crm.spring.rest.fixed.InviteeController
 */
@Service
public class InviteeService {

  private static final List<String> ALLOWED_STATUSES = List.of("ACTIVE", "INACTIVE");

  private final InviteeRepository repository;
  private final InviteeMapper mapper;

  public InviteeService(InviteeRepository repository, InviteeMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  /**
   * Возвращает все объекты (без пагинации; для production рекомендуется Page).
   */
  public List<InviteeResponse> findAll() {
    return repository.findAll().stream().map(mapper::toResponse).toList();
  }

  /**
   * Возвращает объекты по id или выбрасывает EntityNotFoundException (404).
   */
  public InviteeResponse getById(UUID id) {
    Invitee invitee =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()));
    return mapper.toResponse(invitee);
  }

  /**
   * Создаёт объекты. При дубликате email — EmailAlreadyExistsException (409).
   */
  public InviteeResponse create(CreateInviteeRequest request) {
    if (repository.existsByEmail(request.email())) {
      throw new EmailAlreadyExistsException(request.email());
    }
    Invitee invitee = mapper.toEntity(request);
    invitee.setId(UUID.randomUUID());
    invitee.setCreatedAt(Instant.now());
    Invitee saved = repository.save(invitee);
    return mapper.toResponse(saved);
  }

  /**
   * Обновляет статус обхекта. При невалидном статусе — InvalidStatusException (400).
   */
  public InviteeResponse updateStatus(UUID id, UpdateInviteeStatusRequest request) {
    Invitee invitee =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()));
    String status = request.status();
    if (!ALLOWED_STATUSES.contains(status)) {
      throw new InvalidStatusException("Status must be ACTIVE or INACTIVE, got: " + status);
    }
    invitee.setStatus(status);
    Invitee saved = repository.save(invitee);
    return mapper.toResponse(saved);
  }

  /**
   * Удаляет рбъект по id. При отсутствии — EntityNotFoundException (404).
   */
  public void delete(UUID id) {
    Invitee invitee =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()));
    repository.delete(invitee);
  }
}
