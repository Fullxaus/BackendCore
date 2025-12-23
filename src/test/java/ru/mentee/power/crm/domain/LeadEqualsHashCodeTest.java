package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class LeadEqualsHashCodeTest {

    @Test

    void shouldBeReflexive_whenEqualsCalledOnSameObject() {
        // Given
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("ivan@mail.ru", "+7123", address);
        Lead lead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");

        // Then: Объект равен сам себе (isEqualTo использует equals() внутри)
        assertThat(lead).isEqualTo(lead);
    }

    @Test
    void shouldBeSymmetric_whenEqualsCalledOnTwoObjects() {
        // Given
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("ivan@mail.ru", "+7123", address);
        Lead firstLead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");
        Lead secondLead = new Lead(firstLead.id(), firstLead.contact(), firstLead.company(), firstLead.status());

        // Then: Симметричность — порядок сравнения не важен
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(secondLead).isEqualTo(firstLead);
    }

    @Test
    void shouldBeTransitive_whenEqualsChainOfThreeObjects() {
        // Given
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("ivan@mail.ru", "+7123", address);
        Lead firstLead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");
        Lead secondLead = new Lead(firstLead.id(), firstLead.contact(), firstLead.company(), firstLead.status());
        Lead thirdLead = new Lead(firstLead.id(), firstLead.contact(), firstLead.company(), firstLead.status());

        // Then: Транзитивность — если A=B и B=C, то A=C
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(secondLead).isEqualTo(thirdLead);
        assertThat(firstLead).isEqualTo(thirdLead);
    }

    @Test
    void shouldBeConsistent_whenEqualsCalledMultipleTimes() {
        // Given
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("ivan@mail.ru", "+7123", address);
        Lead firstLead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");
        Lead secondLead = new Lead(firstLead.id(), firstLead.contact(), firstLead.company(), firstLead.status());

        // Then: Результат одинаковый при многократных вызовах
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead).isEqualTo(secondLead);
    }

    @Test
    void shouldReturnFalse_whenEqualsComparedWithNull() {
        // Given
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("ivan@mail.ru", "+7123", address);
        Lead lead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");

        // Then: Объект не равен null (isNotEqualTo проверяет equals(null) = false)
        assertThat(lead).isNotEqualTo(null);
    }

    @Test
    void shouldHaveSameHashCode_whenObjectsAreEqual() {
        // Given
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("ivan@mail.ru", "+7123", address);
        Lead firstLead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");
        Lead secondLead = new Lead(firstLead.id(), firstLead.contact(), firstLead.company(), firstLead.status());

        // Then: Если объекты равны, то hashCode должен быть одинаковым
        assertThat(firstLead).isEqualTo(secondLead);
        assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
    }


    @Test
    void shouldWorkInHashMap_whenLeadUsedAsKey() {
        // Given
        Address address = new Address("San Francisco", "123 Main St", "94105");
        Contact contact = new Contact("ivan@mail.ru", "+7123", address);
        Lead keyLead = new Lead(UUID.randomUUID(), contact, "TechCorp", "NEW");
        Lead lookupLead = new Lead(keyLead.id(), keyLead.contact(), keyLead.company(), keyLead.status());

        Map<Lead, String> map = new HashMap<>();
        map.put(keyLead, "CONTACTED");

        // When: Получаем значение по другому объекту с тем же id
        String status = map.get(lookupLead);

        // Then: HashMap нашел значение благодаря equals/hashCode
        assertThat(status).isEqualTo("CONTACTED");
    }

    @Test
    void shouldNotBeEqual_whenContactsAreDifferent() {
        // Given
        Address address1 = new Address("San Francisco", "123 Main St", "94105");
        Contact contact1 = new Contact("ivan@mail.ru", "+7123", address1);
        Lead firstLead = new Lead(UUID.randomUUID(), contact1, "TechCorp", "NEW");

        Address address2 = new Address("New York", "456 Broadway", "10001");
        Contact contact2 = new Contact("ivan@mail.ru", "+7123", address2);
        Lead differentLead = new Lead(UUID.randomUUID(), contact2, "TechCorp", "NEW");

        // Then: Разные контакты = разные объекты (isNotEqualTo использует equals() внутри)
        assertThat(firstLead).isNotEqualTo(differentLead);
    }
}
