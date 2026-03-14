# Code Review Report: InviteeController (problematic)

**Контроллер:** `ru.mentee.power.crm.spring.rest.problematic.InviteeController`  
**Чек-лист:** CODE_REVIEW_CHECKLIST.md (категории API Design, Security, Error Handling, Code Quality)

---

## Issue #1: POST для чтения данных + глагол в URL

**Приоритет:** CRITICAL  
**Категория:** API Design (1.1, 1.3)  
**Местоположение:** InviteeController.java, строки 27–30, метод `getInvitees`

**Что плохо:**
```java
@PostMapping("/getInvitees")
public List<Invitee> getInvitees() {
  return repository.findAll();
}
```

**Почему плохо:** Чтение данных должно выполняться через GET (идемпотентность, кэширование). URL не должен содержать глагол «get» — это RPC-стиль, нарушает REST (ресурс — существительное).

**Как исправить:** `@GetMapping("/invitees")`, возвращать `ResponseEntity<List<InviteeResponse>>` или пагинированный `Page<InviteeResponse>`.

---

## Issue #2: Возврат Entity вместо DTO (getInvitees)

**Приоритет:** CRITICAL  
**Категория:** API Design (1.4), Security (2.2)  
**Местоположение:** InviteeController.java, строки 28–29, метод `getInvitees`

**Что плохо:**
```java
public List<Invitee> getInvitees() {
  return repository.findAll();
}
```

**Почему плохо:** Клиент получает доменную сущность со всеми полями; при расширении модели возможна утечка внутренних полей. Усиливает связь API с моделью домена.

**Как исправить:** Ввести InviteeResponse DTO и маппер; возвращать `List<InviteeResponse>` (или `Page<InviteeResponse>`).

---

## Issue #3: Нет пагинации для списка

**Приоритет:** MAJOR  
**Категория:** API Design (1.5)  
**Местоположение:** InviteeController.java, строка 29, метод `getInvitees`

**Что плохо:**
```java
return repository.findAll();
```

**Почему плохо:** При большом числе записей один запрос возвращает всё — риск перегрузки памяти и сети, нестабильное время ответа.

**Как исправить:** Принять `Pageable`, вызывать `repository.findAll(pageable)`, возвращать `ResponseEntity<Page<InviteeResponse>>`.

---

## Issue #4: getById возвращает Entity и null при отсутствии

**Приоритет:** CRITICAL  
**Категория:** API Design (1.2, 1.4), Error Handling (3.1)  
**Местоположение:** InviteeController.java, строки 35–37, метод `getById`

**Что плохо:**
```java
@GetMapping("/invitees/{id}")
public Invitee getById(@PathVariable UUID id) {
  return repository.findById(id).orElse(null);
}
```

**Почему плохо:** При отсутствии ресурса возвращается null → клиент получает 200 и пустое тело вместо 404 Not Found. Нарушается семантика HTTP. Плюс снова возвращается Entity, а не DTO.

**Как исправить:** В сервисе: `repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()))`. В контроллере возвращать `ResponseEntity.ok(mapper.toResponse(invitee))`. 404 обрабатывать в GlobalExceptionHandler.

---

## Issue #5: Нет валидации входных данных (create)

**Приоритет:** CRITICAL  
**Категория:** Security (2.3)  
**Местоположение:** InviteeController.java, строки 42–45, метод `create`

**Что плохо:**
```java
public Invitee create(@RequestBody Map<String, Object> params) {
  String email = (String) params.get("email");
  String firstName = (String) params.get("firstName");
```

**Почему плохо:** Нет Bean Validation; тип Map не гарантирует наличие и тип полей. Возможны NPE, некорректный email, пустые строки — данные попадают в БД без проверки.

**Как исправить:** Ввести DTO `CreateInviteeRequest` с полями `@NotBlank @Email String email`, `@NotBlank @Size(min=2, max=50) String firstName`. Сигнатура: `create(@Valid @RequestBody CreateInviteeRequest request)`.

---

## Issue #6: SQL injection через конкатенацию

**Приоритет:** CRITICAL  
**Категория:** Security (2.1)  
**Местоположение:** InviteeController.java, строки 47–49, метод `create`

**Что плохо:**
```java
String sql = "SELECT COUNT(*) FROM invitees WHERE email = '" + email + "'";
// repository.executeNativeQuery(sql);
```

**Почему плохо:** Подстановка `email` в SQL строкой даёт классическую SQL-инъекцию (например, `' OR '1'='1`). Даже в комментарии такой код не должен присутствовать как образец.

**Как исправить:** Использовать только параметризованные запросы: Spring Data JPA `existsByEmail(String email)` или JPQL/Native с параметром `?1` / `:email`.

---

## Issue #7: Бизнес-логика и статус кода в create

**Приоритет:** CRITICAL / MAJOR  
**Категория:** API Design (1.2), Code Quality (4.1)  
**Местоположение:** InviteeController.java, строки 51–58, метод `create`

**Что плохо:**
```java
Invitee invitee = new Invitee();
invitee.setId(UUID.randomUUID());
invitee.setEmail(email);
// ...
return repository.save(invitee);
```

**Почему плохо:** Создание сущности и присвоение id/createdAt — бизнес-логика, её место в сервисе. Ответ всегда 200 OK и тело — Entity; по REST создание ресурса должно возвращать 201 Created и заголовок Location.

**Как исправить:** В сервисе: создание Invitee, setId/setCreatedAt, save. В контроллере: `return ResponseEntity.created(URI.create("/api/invitees/" + created.id())).body(mapper.toResponse(created))`.

---

## Issue #8: delete возвращает тело и неправильные статусы

**Приоритет:** CRITICAL  
**Категория:** API Design (1.2), Error Handling (3.1)  
**Местоположение:** InviteeController.java, строки 62–69, метод `delete`

**Что плохо:**
```java
@DeleteMapping("/invitees/{id}")
public Invitee delete(@PathVariable UUID id) {
  Invitee invitee = repository.findById(id).orElse(null);
  if (invitee != null) {
    repository.delete(invitee);
  }
  return invitee;
}
```

**Почему плохо:** DELETE по спецификации при успехе возвращает 204 No Content без тела. Сейчас при успехе — 200 и тело Entity, при отсутствии — 200 и null. Нет явного 404 для «ресурс не найден».

**Как исправить:** Если не найден — выбросить EntityNotFoundException (404). При успешном удалении: `return ResponseEntity.noContent().build()` (204).

---

## Issue #9: Пустой catch и return null в updateStatus

**Приоритет:** CRITICAL  
**Категория:** Error Handling (3.1)  
**Местоположение:** InviteeController.java, строки 72–85, метод `updateStatus`

**Что плохо:**
```java
} catch (Exception e) {
  // Пустой catch
  return null;
}
```

**Почему плохо:** Исключение проглатывается, клиент получает 200 и null. Нет логирования, нет корректного 4xx/5xx, невозможна диагностика в production.

**Как исправить:** Убрать try-catch из контроллера. В сервисе выбрасывать типизированные исключения (EntityNotFoundException, InvalidStatusException). Обработка в GlobalExceptionHandler с логированием и корректным статусом/телом.

---

## Issue #10: Бизнес-ошибка как RuntimeException (500)

**Приоритет:** MAJOR  
**Категория:** Error Handling (3.2)  
**Местоположение:** InviteeController.java, строки 77–79, метод `updateStatus`

**Что плохо:**
```java
} else {
  throw new RuntimeException("Invalid status");
}
```

**Почему плохо:** Невалидный статус — ошибка клиента (400 Bad Request), а не сервера. RuntimeException по умолчанию маппится в 500 Internal Server Error, что вводит в заблуждение и нарушает контракт API.

**Как исправить:** Ввести исключение, например `InvalidStatusException`, обрабатывать в GlobalExceptionHandler и возвращать 400 с сообщением о невалидном статусе.

---

## Issue #11: Бизнес-логика в контроллере (updateStatus)

**Приоритет:** MAJOR  
**Категория:** Code Quality (4.1)  
**Местоположение:** InviteeController.java, строки 74–80, метод `updateStatus`

**Что плохо:**
```java
String status = body.get("status");
if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
  invitee.setStatus(status);
} else {
  throw new RuntimeException("Invalid status");
}
return repository.save(invitee);
```

**Почему плохо:** Проверка и установка статуса, сохранение — бизнес-логика; контроллер должен только принимать запрос и вызывать сервис. Нарушается единая ответственность (SRP).

**Как исправить:** Метод контроллера: `service.updateStatus(id, request)`; вся валидация статуса и обновление — в InviteeService.

---

## Issue #12: Field injection вместо constructor injection

**Приоритет:** MAJOR  
**Категория:** Code Quality  
**Местоположение:** InviteeController.java, строки 20–21

**Что плохо:**
```java
@Autowired
InviteeRepository repository;
```

**Почему плохо:** Field injection затрудняет тестирование (сложнее подставлять моки), зависимости не объявлены как final, неочевидна обязательность зависимости.

**Как исправить:** Один конструктор с параметром `InviteeRepository repository` (и при необходимости InviteeService, InviteeMapper). Поля `private final InviteeRepository repository;`. В Spring 4.3+ один конструктор не требует @Autowired.

---

## Issue #13: Нет проверки авторизации

**Приоритет:** CRITICAL  
**Категория:** Security (2.5)  
**Местоположение:** Весь контроллер

**Что плохо:** Любой вызов API (в т.ч. delete, create) доступен без проверки роли/владельца.

**Почему плохо:** Риск удаления или изменения чужих данных, несанкционированного создания ресурсов.

**Как исправить:** Добавить Spring Security и на чувствительные операции — например, `@PreAuthorize("hasRole('ADMIN')")` на delete или кастомную проверку владельца.

---

## Сводка по приоритетам

| Приоритет  | Количество |
|-----------|-------------|
| CRITICAL  | 8          |
| MAJOR     | 5          |
| MINOR     | 0          |

Всего задокументировано **13 проблем** (требовалось минимум 10).
