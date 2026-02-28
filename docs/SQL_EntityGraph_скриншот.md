# Скриншот консоли: 1 SQL-запрос с JOIN (@EntityGraph)

Чтобы получить в консоли **один** запрос с `LEFT JOIN` и загрузить скриншот на платформу:

---

## Шаг 1. Убедиться, что SQL логируются

В проекте уже включено **`spring.jpa.show-sql: true`** в `application.yml`. Ничего менять не нужно.

---

## Шаг 2. Вызвать код с @EntityGraph (один запрос с JOIN)

**Самый простой способ — запустить тест:**

1. Откройте класс **`DealProductIntegrationTest`**  
   (`src/test/java/ru/mentee/power/crm/repository/DealProductIntegrationTest.java`).

2. Запустите **только** тест **`testEntityGraphSolvesNPlusOne`**:
   - в IDE: правый клик по методу `testEntityGraphSolvesNPlusOne` → **Run**;
   - или в терминале из корня проекта:
     ```bash
     .\gradlew test --tests "ru.mentee.power.crm.repository.DealProductIntegrationTest.testEntityGraphSolvesNPlusOne"
     ```

3. В консоли (IDE или терминал) найдите блок с **одним** SQL-запросом, где есть:
   - `select ... from deals de1_0`
   - `left join deal_product dp1_0 on de1_0.id=dp1_0.deal_id`
   - `left join products p1_0 on dp1_0.product_id=p1_0.id`
   - `where de1_0.id=?`

   Это и есть запрос, который даёт @EntityGraph (один запрос вместо N+1).

---

## Шаг 3. Скриншот

Сделайте скриншот того фрагмента консоли, где виден **именно этот один** SELECT с JOIN (желательно, чтобы было видно и строку `Hibernate:` и сам SQL).

---

## Шаг 4. Загрузка на платформу

Прикрепите этот скриншот к ответу на платформу как «SQL логи с @EntityGraph (1 запрос с JOIN)».
