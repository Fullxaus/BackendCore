package ru.mentee.power.crm.spring.rest.fixed;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.spring.rest.fixed.dto.CreateInviteeRequest;
import ru.mentee.power.crm.spring.rest.fixed.dto.InviteeResponse;
import ru.mentee.power.crm.spring.rest.fixed.dto.UpdateInviteeStatusRequest;

/**
 * REST API (отрефакторенная версия по CODE_REVIEW_REPORT.md).
 *
 * <p>Использует правильные HTTP методы, статус коды, DTO, валидацию, сервисный слой и
 * централизованную обработку ошибок через GlobalExceptionHandler.
 *
 * @see ru.mentee.power.crm.spring.rest.fixed.InviteeService
 * @see ru.mentee.power.crm.spring.rest.fixed.InviteeMapper
 */
@RestController
@RequestMapping("/api/invitees")
public class InviteeController {

  private final InviteeService inviteeService;

  public InviteeController(InviteeService inviteeService) {
    this.inviteeService = inviteeService;
  }

  /**
   * Возвращает список .
   *
   * @return 200 OK и список InviteeResponse
   */
  @GetMapping
  public ResponseEntity<List<InviteeResponse>> getInvitees() {
    List<InviteeResponse> list = inviteeService.findAll();
    return ResponseEntity.ok(list);
  }

  /**
   * Возвращает приглашённого по id. При отсутствии — 404 (обрабатывается GlobalExceptionHandler).
   *
   * @param id UUID приглашённого
   * @return 200 OK и InviteeResponse
   */
  @GetMapping("/{id}")
  public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
    InviteeResponse response = inviteeService.getById(id);
    return ResponseEntity.ok(response);
  }

  /**
   * Создаёт объект. При дубликате email — 409 Conflict.
   *
   * @param request валидируемый DTO
   * @return 201 Created, заголовок Location и тело InviteeResponse
   */
  @PostMapping
  public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
    InviteeResponse created = inviteeService.create(request);
    URI location = URI.create("/api/invitees/" + created.id());
    return ResponseEntity.created(location).body(created);
  }

  /**
   * Обновляет статус. При невалидном статусе — 400 Bad Request.
   *
   * @param id UUID приглашённого
   * @param request статус ACTIVE или INACTIVE
   * @return 200 OK и InviteeResponse
   */
  @PutMapping("/{id}/status")
  public ResponseEntity<InviteeResponse> updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateInviteeStatusRequest request) {
    InviteeResponse updated = inviteeService.updateStatus(id, request);
    return ResponseEntity.ok(updated);
  }

  /**
   * Удаляет объект. При отсутствии — 404. При успехе — 204 No Content.
   *
   * @param id UUID приглашённого
   * @return 204 No Content
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    inviteeService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
