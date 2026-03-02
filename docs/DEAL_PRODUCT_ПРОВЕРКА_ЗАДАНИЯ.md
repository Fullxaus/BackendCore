# Проверка задания: Deal↔Product через junction table и N+1

## 1. Откуда что вытекает (связи в проекте)

### Сущности и таблицы
- **DealEntity** (`ru.mentee.power.crm.entity`) → таблица **deals** (id, lead_id, amount, status, created_at).
- **Product** (`ru.mentee.power.crm.domain`) → таблица **products** (id, name, sku, price, active).
- **DealProduct** (`ru.mentee.power.crm.entity`) → таблица **deal_product** (id, deal_id, product_id, quantity, unit_price). Связь N:M между Deal и Product.

### Доменная модель vs JPA
- **Deal** (domain, `ru.mentee.power.crm.domain.Deal`) — доменная модель сделки, используется в сервисах/контроллерах. Не содержит продуктов.
- **DealEntity** — JPA-сущность для таблицы `deals`, содержит `List<DealProduct> dealProducts` и методы `addDealProduct` / `removeDealProduct`.
- Сохранение сделок в БД идёт через **JpaDealRepository** → **DealEntityRepository**: доменный `Deal` конвертируется в `DealEntity`, сохраняется репозиторием.

### Репозитории
- **DealEntityRepository** (`entity`) — `JpaRepository<DealEntity, UUID>`, базовые методы (findById, findByStatus и т.д.).
- **DealJpaRepository** (`repository`) — расширяет `JpaRepository<DealEntity, UUID>`, добавляет **findDealWithProducts(UUID)** с **@EntityGraph** для загрузки Deal + DealProducts + Products одним запросом (решение N+1).
- **ProductJpaRepository** (`spring.repository`) — `JpaRepository<Product, UUID>`.
- **JpaDealRepository** — реализация интерфейса `DealRepository`, внутри использует **DealEntityRepository** (не DealJpaRepository). Для сценариев «сделка с продуктами» нужно использовать **DealJpaRepository.findDealWithProducts**.

### Миграции
- **db.changelog-master.yaml** подключает 001–005 (companies, leads, contacts, deals, products), затем **006_create_deal_product.sql** и **007_create_deal_product_if_missing.sql**.
- Таблица **deal_product** создаётся с колонками: id, deal_id, product_id, quantity, unit_price; FK на deals и products с ON DELETE CASCADE; UNIQUE(deal_id, product_id); индексы по deal_id и product_id.

---

## 2. Проверка Acceptance Criteria

| Критерий | Статус | Где проверено |
|----------|--------|----------------|
| **Given Deal и Product существуют → When создаём DealProduct(deal, product, quantity=3, unitPrice=81000) → Then связь сохраняется в deal_product** | ✅ | `DealProductIntegrationTest.testSaveDealWithProducts()`: создаётся Deal, два Product, два DealProduct (один с quantity=3, unitPrice=81000), `deal.addDealProduct()`, `dealRepository.save(deal)`; после загрузки проверяется `getDealProducts().size()`, quantity и unitPrice. |
| **Given Deal.getDealProducts() → When вызывается метод → Then возвращается List с загруженными Product** | ✅ | В том же тесте: `fromDb.getDealProducts()` возвращает список; у каждого `DealProduct` вызывается `getProduct()` и проверяется `getSku()` — продукт загружен. При использовании `findDealWithProducts` продукты подтягиваются через @EntityGraph. |
| **Given @EntityGraph на findById → When загружаем Deal → Then 1 SQL с JOIN вместо N+1** | ✅ | `DealJpaRepository.findDealWithProducts(id)` с `@EntityGraph(attributePaths = {"dealProducts", "dealProducts.product"})` и `@Query("SELECT d FROM DealEntity d WHERE d.id = :id")`. Тест `testEntityGraphSolvesNPlusOne` проверяет загрузку через этот метод; при show-sql=true в логе один запрос с LEFT JOIN deal_product и products. |
| **Given junction table → When проверяем через pgAdmin → Then видны deal_id, product_id, quantity, unit_price** | ✅ | Миграции 006/007 создают таблицу **deal_product** с колонками: **deal_id**, **product_id**, **quantity**, **unit_price** (и id). В pgAdmin после применения миграций эти столбцы отображаются. |

---

## 3. Итог

- Связь Deal↔Product реализована через **DealProduct** (junction entity) и таблицу **deal_product** (Liquibase 006/007).
- **DealEntity** и **Product** обновлены: двунаправленная связь через `List<DealProduct>`, у Deal — `addDealProduct` / `removeDealProduct`.
- N+1 решён методом **findDealWithProducts** с **@EntityGraph**; один SQL с JOIN вместо серии запросов.
- Тесты **DealProductIntegrationTest** покрывают сохранение в deal_product, возврат списка с загруженными продуктами и использование EntityGraph.
