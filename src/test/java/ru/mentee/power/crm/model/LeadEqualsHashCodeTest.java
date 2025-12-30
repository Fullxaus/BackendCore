package ru.mentee.power.crm.model;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class LeadEqualsHashCodeTest {

    @Test
    void shouldBeReflexive_whenEqualsCalledOnSameObject() {
        // Given
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead lead = new Lead(id, contact, "TechCorp", "NEW");

        // Then: Объект равен сам себе (isEqualTo использует equals() внутри)
        assertThat(lead).isEqualTo(lead);
    }

    @Test
    void shouldBeSymmetric_whenEqualsCalledOnTwoObjects() {
        // Given
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead firstLead = new Lead(id, contact, "TechCorp", "NEW");
        Lead secondLead = new Lead(id, contact, "TechCorp", "NEW");

        // Then: Симметричность — порядок сравнения не важен
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(secondLead).isEqualTo(firstLead);
    }

    @Test
    void shouldBeTransitive_whenEqualsChainOfThreeObjects() {
        // Given
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead firstLead = new Lead(id, contact, "TechCorp", "NEW");
        Lead secondLead = new Lead(id, contact, "TechCorp", "NEW");
        Lead thirdLead = new Lead(id, contact, "TechCorp", "NEW");

        // Then: Транзитивность — если A=B и B=C, то A=C
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(secondLead).isEqualTo(thirdLead);
        assertThat(firstLead).isEqualTo(thirdLead);
    }

    @Test
    void shouldBeConsistent_whenEqualsCalledMultipleTimes() {
        // Given
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead firstLead = new Lead(id, contact, "TechCorp", "NEW");
        Lead secondLead = new Lead(id, contact, "TechCorp", "NEW");

        // Then: Результат одинаковый при многократных вызовах
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead).isEqualTo(secondLead);
    }

    @Test
    void shouldReturnFalse_whenEqualsComparedWithNull() {
        // Given
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead lead = new Lead(id, contact, "TechCorp", "NEW");

        // Then: Объект не равен null (isNotEqualTo проверяет equals(null) = false)
        assertThat(lead).isNotEqualTo(null);
    }

    @Test
    void shouldHaveSameHashCode_whenObjectsAreEqual() {
        // Given
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead firstLead = new Lead(id, contact, "TechCorp", "NEW");
        Lead secondLead = new Lead(id, contact, "TechCorp", "NEW");

        // Then: Если объекты равны, то hashCode должен быть одинаковым
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
    }


    @Test
    void shouldWorkInHashMap_whenLeadUsedAsKey() {
        // Given
        UUID id = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead keyLead = new Lead(id, contact, "TechCorp", "NEW");
        Lead lookupLead = new Lead(id, contact, "TechCorp", "NEW");

        Map<Lead, String> map = new HashMap<>();
        map.put(keyLead, "CONTACTED");

        // When: Получаем значение по другому объекту с тем же id
        String status = map.get(lookupLead);

        // Then: HashMap нашел значение благодаря equals/hashCode
        assertThat(status).isEqualTo("CONTACTED");
    }

    @Test
    void shouldNotBeEqual_whenIdsAreDifferent() {
        // Given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Contact contact = new Contact("ivan@mail.ru", "+7123", new Address("New York", "456 Broadway", "10001"));
        Lead firstLead = new Lead(id1, contact, "TechCorp", "NEW");
        Lead differentLead = new Lead(id2, contact, "TechCorp", "NEW");

        // Then: Разные id = разные объекты (isNotEqualTo использует equals() внутри)
        assertThat(firstLead).isNotEqualTo(differentLead);
    }
}
