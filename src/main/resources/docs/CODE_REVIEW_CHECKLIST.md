# REST Controller Code Review Checklist

Использование: при ревью контроллера прохожу по каждой категории, сверяю код с пунктами чек-листа, фиксирую номер строки и метод для каждого найденного issue. Результат — структурированный CODE_REVIEW_REPORT.md с приоритетами.

---

## Категория 1: API Design (5 проблем)

### 1.1 Неправильные HTTP методы

**Приоритет:** CRITICAL  
**Что искать:** POST используется для чтения данных, GET для модификации

**Плохо:**
```java
@PostMapping("/getInvitees") // Глагол в URL + неправильный метод
public List<Invitee> getInvitees() { ... }
```

**Хорошо:**
```java
@GetMapping("/invitees") // Существительное + правильный HTTP метод
public ResponseEntity<List<InviteeResponse>> getInvitees() { ... }
```

### 1.2 Неправильные статус коды

**Приоритет:** CRITICAL  
**Что искать:** 200 для всех операций, отсутствие 201/204/404

**Плохо:**
```java
@PostMapping("/invitees")
public Invitee create(@RequestBody Invitee invitee) {
    return service.save(invitee); // Всегда 200 OK
}
```

**Хорошо:**
```java
@PostMapping("/invitees")
public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
    InviteeResponse created = service.create(request);
    URI location = URI.create("/api/invitees/" + created.id());
    return ResponseEntity.created(location).body(created); // 201 Created + Location header
}
```

### 1.3 Плохой naming: глаголы в URL

**Приоритет:** MAJOR  
**Что искать:** /getInvitees, /createInvitee, /updateInviteeStatus в URLs

**Плохо:**
```java
@GetMapping("/getInvitees") // RPC стиль
@PostMapping("/createInvitee")
```

**Хорошо:**
```java
@GetMapping("/invitees") // RESTful стиль
@PostMapping("/invitees")
```

### 1.4 Entity вместо DTO в response

**Приоритет:** CRITICAL (security + coupling)  
**Что искать:** Возврат JPA Entity / доменных объектов напрямую

**Плохо:**
```java
@GetMapping("/invitees/{id}")
public Invitee getById(@PathVariable UUID id) {
    return repository.findById(id).orElseThrow(); // Entity, internal fields
}
```

**Хорошо:**
```java
@GetMapping("/invitees/{id}")
public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
    Invitee invitee = service.getById(id);
    return ResponseEntity.ok(mapper.toResponse(invitee)); // DTO
}
```

### 1.5 Нет пагинации для списков

**Приоритет:** MAJOR (performance)  
**Что искать:** GET endpoints возвращающие List без page/size

**Плохо:**
```java
@GetMapping("/invitees")
public List<Invitee> getAll() {
    return repository.findAll(); // Может вернуть 10,000 записей
}
```

**Хорошо:**
```java
@GetMapping("/invitees")
public ResponseEntity<Page<InviteeResponse>> getAll(
    @PageableDefault(size = 20) Pageable pageable) {
    Page<Invitee> page = repository.findAll(pageable);
    return ResponseEntity.ok(page.map(mapper::toResponse));
}
```

---

## Категория 2: Security (5 проблем)

### 2.1 SQL injection через конкатенацию

**Приоритет:** CRITICAL  
**Что искать:** Конкатенация строк в SQL

**Плохо:**
```java
String sql = "SELECT * FROM invitees WHERE email = '" + email + "'";
```

**Хорошо:**
```java
Invitee findByEmail(String email); // Spring Data JPA
// или PreparedStatement с ? и setString(1, email)
```

### 2.2 Exposure внутренних полей

**Приоритет:** CRITICAL  
**Что искать:** password, internalId, version в response

**Плохо:** Возврат Entity с полями, не предназначенными для API.

**Хорошо:** DTO (record или класс) только с нужными полями, маппер Entity → DTO.

### 2.3 Нет валидации входных данных

**Приоритет:** CRITICAL  
**Что искать:** @RequestBody без @Valid, нет Bean Validation

**Плохо:**
```java
@PostMapping("/invitees")
public Invitee create(@RequestBody Invitee invitee) { ... }
```

**Хорошо:**
```java
@PostMapping("/invitees")
public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) { ... }
```
CreateInviteeRequest с @NotBlank, @Email, @Size.

### 2.4 Stack trace в error response

**Приоритет:** CRITICAL  
**Что искать:** printStackTrace или stack trace в JSON ответе клиенту

**Хорошо:** GlobalExceptionHandler логирует исключение на сервере, клиенту возвращает ProblemDetail без stack trace.

### 2.5 Missing authorization checks

**Приоритет:** CRITICAL  
**Что искать:** Отсутствие @PreAuthorize / проверок прав

**Плохо:** delete() доступен любому.  
**Хорошо:** @PreAuthorize("hasRole('ADMIN') or ...") на удалении и других чувствительных операциях.

---

## Категория 3: Error Handling (4 проблемы)

### 3.1 Пустые catch блоки

**Приоритет:** MAJOR  
**Что искать:** catch (Exception e) {} или только комментарий

**Плохо:** return null в catch, клиент не получает 4xx/5xx.

**Хорошо:** Выброс типизированного exception, обработка в GlobalExceptionHandler.

### 3.2 500 на бизнес-ошибки вместо 4xx

**Приоритет:** MAJOR  
**Что искать:** RuntimeException / 500 для дубликата email и т.п.

**Хорошо:** EmailAlreadyExistsException → 409 в GlobalExceptionHandler.

### 3.3 Generic error messages без деталей

**Приоритет:** MINOR  
**Что искать:** "Error occurred" без полей errors для валидации.

**Хорошо:** RFC 7807 Problem Detail с полем errors (field → message).

### 3.4 Нет логирования ошибок

**Приоритет:** MAJOR  
**Что искать:** Обработка exception без logger.error(..., ex).

**Хорошо:** В GlobalExceptionHandler — logger.error("Unexpected error", ex), затем ответ клиенту без trace.

---

## Категория 4: Code Quality (4 проблемы)

### 4.1 Бизнес-логика в контроллере

**Приоритет:** MAJOR (SRP)  
**Что искать:** if/else по бизнес-правилам, расчёты, вызов нескольких репозиториев.

**Хорошо:** Контроллер только вызывает service.create(request), вся логика в InviteeService.

### 4.2 Дублирование кода

**Приоритет:** MAJOR (DRY)  
**Что искать:** Одинаковый try-catch или маппинг в каждом методе.

**Хорошо:** Единый GlobalExceptionHandler, маппинг в Mapper.

### 4.3 God Controller

**Приоритет:** MINOR  
**Что искать:** 20+ методов несвязанных операций в одном классе.

**Хорошо:** Разделение по bounded context (InviteeController, InviteeConversionController и т.д.).

### 4.4 Hardcoded values

**Приоритет:** MINOR  
**Что искать:** Magic numbers, захардкоженные URL, роли в коде.

**Хорошо:** @PageableDefault, application.yml, @ConfigurationProperties.
