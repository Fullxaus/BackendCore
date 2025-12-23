package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class ContactTest {

    @Test
    void shouldCreateContact_whenValidData() {
        // Создать Contact с firstName="John", lastName="Doe", email="john@example.com"
        Contact contact = new Contact("John", "Doe", "john@example.com");

        // Проверить что все компоненты возвращают правильные значения
        assertThat(contact.firstName()).isEqualTo("John");
        assertThat(contact.lastName()).isEqualTo("Doe");
        assertThat(contact.email()).isEqualTo("john@example.com");
    }

    @Test
    void shouldBeEqual_whenSameData() {
        // Создать два Contact с одинаковыми данными
        Contact contact1 = new Contact("John", "Doe", "john@example.com");
        Contact contact2 = new Contact("John", "Doe", "john@example.com");

        // Проверить что они равны через equals
        assertThat(contact1).isEqualTo(contact2);

        // Проверить что hashCode одинаковый
        assertThat(contact1.hashCode()).isEqualTo(contact2.hashCode());
    }

    @Test
    void shouldNotBeEqual_whenDifferentData() {
        // Создать два Contact с разными данными
        Contact contact1 = new Contact("John", "Doe", "john@example.com");
        Contact contact2 = new Contact("Jane", "Doe", "jane@example.com");

        // Проверить что они НЕ равны
        assertThat(contact1).isNotEqualTo(contact2);
    }
}


