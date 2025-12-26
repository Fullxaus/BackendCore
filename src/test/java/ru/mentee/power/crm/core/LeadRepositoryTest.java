package ru.mentee.power.crm.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.mentee.power.crm.domain.Lead;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

public class LeadRepositoryTest {

    @Test
    @DisplayName("Should automatically deduplicate leads by id")
    void shouldDeduplicateLeadsById() {
        // Given
        LeadRepository repository = new LeadRepository();
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", "status");

        // When
        boolean added1 = repository.add(lead);
        boolean added2 = repository.add(lead);

        // Then
        assertThat(repository.size()).isEqualTo(1);
        assertThat(added1).isTrue();
        assertThat(added2).isFalse();
    }

    @Test
    @DisplayName("Should allow different leads with different ids")
    void shouldAllowDifferentLeads() {
        // Given
        LeadRepository repository = new LeadRepository();
        Lead lead1 = new Lead(UUID.randomUUID(), "email1", "phone1", "company1", "status1");
        Lead lead2 = new Lead(UUID.randomUUID(), "email2", "phone2", "company2", "status2");

        // When
        boolean added1 = repository.add(lead1);
        boolean added2 = repository.add(lead2);

        // Then
        assertThat(repository.size()).isEqualTo(2);
        assertThat(added1).isTrue();
        assertThat(added2).isTrue();
    }

    @Test
    @DisplayName("Should find existing lead through contains")
    void shouldFindExistingLead() {
        // Given
        LeadRepository repository = new LeadRepository();
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", "status");
        repository.add(lead);

        // When
        boolean contains = repository.contains(lead);

        // Then
        assertThat(contains).isTrue();
    }

    @Test
    @DisplayName("Should return unmodifiable set from findAll")
    void shouldReturnUnmodifiableSet() {
        // Given
        LeadRepository repository = new LeadRepository();
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", "status");
        repository.add(lead);

        // When
        Set<Lead> leads = repository.findAll();

        // Then
        assertThatThrownBy(() -> leads.add(new Lead(UUID.randomUUID(), "email2", "phone2", "company2", "status2")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should perform contains() faster than ArrayList")
    void shouldPerformFasterThanArrayList() {
        // Given
        int iterations = 1000;
        int count = 10000;
        Lead lead = new Lead(UUID.randomUUID(), "email", "phone", "company", "status");

        Set<Lead> set = new HashSet<>();
        for (int i = 0; i < count; i++) {
            set.add(lead);
        }

        List<Lead> list = new ArrayList<>(set);

        // When
        long startSet = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            set.contains(lead);
        }
        long durationSet = System.nanoTime() - startSet;

        long startList = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            list.contains(lead);
        }
        long durationList = System.nanoTime() - startList;

        // Then
        assertThat(durationSet).isLessThan(durationList);
    }

}
