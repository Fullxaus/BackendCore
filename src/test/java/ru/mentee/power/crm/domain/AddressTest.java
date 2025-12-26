package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class AddressTest {

    @Test
    void shouldCreateAddress_whenValidData() {
        // Создать Address с city="San Francisco", street="123 Main St", zip="94105"
        Address address = new Address("San Francisco", "123 Main St", "94105");

        // Проверить что все геттеры возвращают правильные значения
        assertThat(address.city()).isEqualTo("San Francisco");
        assertThat(address.street()).isEqualTo("123 Main St");
        assertThat(address.zip()).isEqualTo("94105");
    }

    @Test
    void shouldBeEqual_whenSameData() {
        // Создать два Address с одинаковыми данными
        Address address1 = new Address("San Francisco", "123 Main St", "94105");
        Address address2 = new Address("San Francisco", "123 Main St", "94105");

        // Проверить что они равны через equals
        assertThat(address1).isEqualTo(address2);

        // Проверить что hashCode одинаковый
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @Test
    void shouldThrowException_whenCityIsNull() {
        // Проверить что создание Address с city=null бросает IllegalArgumentException
        assertThatThrownBy(() -> new Address(null, "123 Main St", "94105"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenCityIsBlank() {
        // Проверить что создание Address с city="" бросает IllegalArgumentException
        assertThatThrownBy(() -> new Address("", "123 Main St", "94105"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenZipIsBlank() {
        // Проверить что создание Address с zip="" бросает IllegalArgumentException
        assertThatThrownBy(() -> new Address("San Francisco", "123 Main St", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenZipIsNull() {
        // Проверить что создание Address с zip=null бросает IllegalArgumentException
        assertThatThrownBy(() -> new Address("San Francisco", "123 Main St", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
