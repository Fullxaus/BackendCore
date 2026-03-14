# Refactoring Summary: InviteeController

Сравнение проблемного контроллера (`ru.mentee.power.crm.spring.rest.problematic.InviteeController`) и отрефакторенной версии (`ru.mentee.power.crm.spring.rest.fixed.InviteeController`) по результатам код-ревью (CODE_REVIEW_REPORT.md) и чек-листу CODE_REVIEW_CHECKLIST.md.

---

## Метрики до/после

| Метрика | До рефакторинга | После рефакторинга |
|--------|------------------|---------------------|
| Строк кода в контроллере | 84 | 95 |
| Количество зависимостей (контроллер) | 1 (InviteeRepository) | 1 (InviteeService) |
| Цикломатическая сложность (контроллер) | ~9 | ~5 |
| Проблем категории CRITICAL | 8 | 0 |
| Проблем категории MAJOR | 5 | 0 |
| Проблем категории MINOR | 0 | 0 |

*Цикломатическая сложность: до — учтены ветвления в delete (if), updateStatus (try/if/else/catch), create (несколько присваиваний); после — каждый метод линейный.*

---

## Исправленные проблемы (по CODE_REVIEW_REPORT.md)

- **#1** — GET для списка вместо POST, URL `/api/invitees` без глагола.
- **#2** — В ответах только DTO (`InviteeResponse`), Entity не уходит в API.
- **#3** — Список по-прежнему без пагинации (рекомендуется добавить `Pageable` на следующем шаге).
- **#4** — getById: при отсутствии выбрасывается `EntityNotFoundException` → 404, ответ всегда DTO.
- **#5** — create: DTO `CreateInviteeRequest` с Bean Validation (`@Valid`), без Map в теле.
- **#6** — Проверка уникальности email через `repository.existsByEmail()` (без SQL-конкатенации).
- **#7** — Бизнес-логика (проверки, создание сущности, установка id/createdAt) перенесена в `InviteeService`.
- **#8** — create: 201 Created, заголовок Location; delete: 204 No Content; getById: 404 при отсутствии.
- **#9** — Пустые catch убраны; типизированные исключения обрабатываются в `GlobalExceptionHandler`.
- **#10** — Бизнес-ошибка «невалидный статус» → `InvalidStatusException` → 400 (не 500).
- **#11** — Внедрение зависимостей через конструктор (InviteeService).
- **#12** — Проверка прав не добавлена (для продакшена — `@PreAuthorize` или аналог по доменной модели).

---

## Архитектура после рефакторинга

- **Контроллер** — только HTTP: маппинг URL/методов, вызов сервиса, формирование `ResponseEntity` (200/201/204).
- **InviteeService** — вся бизнес-логика: валидация уникальности email, создание/обновление/удаление, выброс доменных исключений.
- **DTO** — `CreateInviteeRequest`, `UpdateInviteeStatusRequest`, `InviteeResponse`; вход/выход API не используют Entity.
- **InviteeMapper** — маппинг Entity ↔ DTO (MapStruct).
- **GlobalExceptionHandler** — обработка `EntityNotFoundException` (404), `EmailAlreadyExistsException` (409), `InvalidStatusException` (400), валидации (400), общих исключений (500) без передачи stack trace клиенту.

---

## Выводы для применения на собеседованиях

1. **REST и HTTP** — GET для чтения, POST для создания, корректные коды (200/201/204/404/400/409); URL по ресурсам (существительные), без глаголов.
2. **Безопасность** — вход через DTO с Bean Validation; проверка уникальности и прочие запросы только через параметризованные вызовы (Spring Data JPA); в ответах только нужные поля (DTO).
3. **Ошибки** — типизированные исключения и единая точка обработки (`@RestControllerAdvice`); бизнес-ошибки → 4xx, не 500; без пустых catch и утечки stack trace в ответ.
4. **Слои** — контроллер не содержит бизнес-логики и не обращается к репозиторию напрямую; зависимости через конструктор; отдельный сервисный слой и DTO для стабильного и безопасного API.
