package ru.mentee.power.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadDomainRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeadServiceMockTest {

    @Mock
    private LeadDomainRepository mockRepository;

    private LeadService service;

    @BeforeEach
    void setUp() {
        service = new LeadService(mockRepository);
    }

    @Test
    void shouldCallRepositorySave_whenAddingNewLead() {
        // Given: Repository возвращает пустой Optional (email уникален)
        when(mockRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        // When: настраиваем save
        Address address = new Address("City", "Street", "12345");
        doNothing().when(mockRepository).save(any(Lead.class));

        // When: вызываем бизнес-метод
        Lead result = service.addLead("new@example.com", "Company", LeadStatus.NEW, address, "1234567890");

        // Then: проверяем что Repository.save() был вызван ровно 1 раз
        verify(mockRepository, times(1)).save(any(Lead.class));

        // Then: проверяем результат
        assertThat(result.contact().email()).isEqualTo("new@example.com");
    }


    @Test
    void shouldNotCallSave_whenEmailExists() {
        // Given: Repository возвращает существующий Lead
        Lead existingLead = new Lead(
                UUID.randomUUID(),
                new Contact("existing@example.com", "1234567890", new Address("City", "Street", "12345")),
                "Existing Company",
                LeadStatus.NEW.name()
        );
        when(mockRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(existingLead));

        // When/Then: ожидаем исключение
        assertThatThrownBy(() ->
                service.addLead("existing@example.com", "New Company", LeadStatus.NEW, null, null)
        ).isInstanceOf(IllegalStateException.class);

        // Then: save() НЕ должен быть вызван
        verify(mockRepository, never()).save(any(Lead.class));
    }


    @Test
    void shouldCallFindByEmail_beforeSave() {
        // Given
        when(mockRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        doNothing().when(mockRepository).save(any(Lead.class));

        // When
        service.addLead("test@example.com", "Company", LeadStatus.NEW, new Address("City", "Street", "12345"), "1234567890");

        // Then: проверяем порядок вызовов
        var inOrder = inOrder(mockRepository);
        inOrder.verify(mockRepository).findByEmail("test@example.com");
        inOrder.verify(mockRepository).save(any(Lead.class));
    }
}
